/*
Drawboard - Java applet used to make graphical teleconferences
Copyright (C) 2001  Tomek "TomasH" Zielinski, tomash@fidonet.org.pl

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package drawboard;

/*
 * This file was adapted from Jason Marshall's PNGImageProducer.java
 *
 * Copyright (c) 1997, Jason Marshall.  All Rights Reserved
 *
 * The author makes no representations or warranties regarding the suitability,
 * reliability or stability of this code.  This code is provided AS IS.  The
 * author shall not be liable for any damages suffered as a result of using,
 * modifying or redistributing this software or any derivitives thereof.
 * Permission to use, reproduce, modify and/or (re)distribute this software is
 * hereby granted.
 */

import java.io.*;
import java.util.*;
import java.util.zip.*;

/** Converts an InputStream carrying a PNG image into an ARGB int[] */
public class PngDecoder {

   // Public Methods ///////////////////////////////////////////////////////////////////////////////

   /** returns the ARGB int[] representing the last image processed */
   public final int[] getData() { return data; }

   /** returns the width of the last image processed */
   public final int getWidth() { return width; }

   /** returns the height of the last image processed */
   public final int getHeight() { return height; }

   /** process a PNG as an inputstream; returns null if there is an error */
   public static PngDecoder decode(InputStream is) throws IOException {
     return new PngDecoder(is);
   }

   private PngDecoder(InputStream is) throws IOException {
     underlyingStream = is;
     target_offset = 0;
     inputStream = new DataInputStream(underlyingStream);

     // consume the header
     if ((inputStream.read() != 137) || (inputStream.read() != 80) || (inputStream.read() != 78) || (inputStream.read() != 71) ||
         (inputStream.read() != 13) || (inputStream.read() != 10) || (inputStream.read() != 26) || (inputStream.read() != 10)) {
         System.out.println("PNG: error: input file is not a PNG file");
         data = new int[] { };
         width = height = 0;
         return;
     }

     DONE: while (!error) {
         if (needChunkInfo) {
             chunkLength = inputStream.readInt();
             chunkType = inputStream.readInt();
             needChunkInfo = false;
         }

         switch (chunkType) {
         case CHUNK_bKGD: inputStream.skip(chunkLength); break;
         case CHUNK_cHRM: inputStream.skip(chunkLength); break;
         case CHUNK_gAMA: inputStream.skip(chunkLength); break;
         case CHUNK_hIST: inputStream.skip(chunkLength); break;
         case CHUNK_pHYs: inputStream.skip(chunkLength); break;
         case CHUNK_sBIT: inputStream.skip(chunkLength); break;
         case CHUNK_tEXt: inputStream.skip(chunkLength); break;
         case CHUNK_zTXt: inputStream.skip(chunkLength); break;
         case CHUNK_tIME: inputStream.skip(chunkLength); break;

         case CHUNK_IHDR: handleIHDR(); break;
         case CHUNK_PLTE: handlePLTE(); break;
         case CHUNK_tRNS: handletRNS(); break;

         case CHUNK_IDAT: handleIDAT(); break;

         case CHUNK_IEND: break DONE;
         default:
             System.err.println("unrecognized chunk type " +
                                Integer.toHexString(chunkType) + ". skipping");
             inputStream.skip(chunkLength);
         }

         int crc = inputStream.readInt();
         needChunkInfo = true;
     }
   }

   // Chunk Handlers ///////////////////////////////////////////////////////////////////////

  /** handle data chunk */
  private void handleIDAT() throws IOException {
    if (width == -1 || height == -1) throw new IOException("never got image width/height");
    switch (depth) {
    case 1: mask = 0x1; break;
    case 2: mask = 0x3; break;
    case 4: mask = 0xf; break;
    case 8: case 16: mask = 0xff; break;
    default: mask = 0x0; break;
    }
    if (depth < 8) smask = mask << depth;
    else smask = mask << 8;

    int count = width * height;

    switch (colorType) {
    case 0:
    case 2:
    case 6:
    case 4:
        ipixels = new int[count];
        pixels = ipixels;
        break;
    case 3:
        bpixels = new byte[count];
        pixels = bpixels;
        break;
    default:
        throw new IOException("Image has unknown color type");
    }
    if (interlaceMethod != 0) multipass = true;
    readImageData();
  }

  /** handle header chunk */
  private void handleIHDR() throws IOException {
    if (headerFound) throw new IOException("Extraneous IHDR chunk encountered.");
    if (chunkLength != 13) throw new IOException("IHDR chunk length wrong: " + chunkLength);
    width = inputStream.readInt();
    height = inputStream.readInt();
    depth = inputStream.read();
    colorType = inputStream.read();
    compressionMethod = inputStream.read();
    filterMethod = inputStream.read();
    interlaceMethod = inputStream.read();
  }

  /** handle pallette chunk */
  private void handlePLTE() throws IOException {
    if (colorType == 3) {
        palette = new byte[chunkLength];
        inputStream.readFully(palette);
    } else {
        // Ignore suggested palette
        inputStream.skip(chunkLength);
    }
  }

  /** handle transparency chunk; modifies palette */
  private void handletRNS() throws IOException {
    int chunkLen = chunkLength;
    if (palette == null) {
        inputStream.skip(chunkLength);
        return;
    }
    int len = palette.length;
    if (colorType == 3) {
        transparency = true;

        int transLength = len/3;
        byte[] trans = new byte[transLength];
        for (int i = 0; i < transLength; i++) trans[i] = (byte) 0xff;
        inputStream.readFully(trans, 0, chunkLength);

        byte[] newPalette = new byte[len + transLength];
        for (int i = newPalette.length; i > 0;) {
            newPalette[--i] = trans[--transLength];
            newPalette[--i] = palette[--len];
            newPalette[--i] = palette[--len];
            newPalette[--i] = palette[--len];
        }
        palette = newPalette;

    } else {
        inputStream.skip(chunkLength);
    }
  }

  /// Helper functions for IDAT ///////////////////////////////////////////////////////////////////////////////////////////

  /** Read Image data in off of a compression stream */
  private void readImageData() throws IOException {
    InputStream dataStream = new SequenceInputStream(new IDATEnumeration(this));
    DataInputStream dis = new DataInputStream(new BufferedInputStream(new InflaterInputStream(dataStream, new Inflater())));
    int bps, filterOffset;
    switch (colorType) {
      case 0: case 3: bps = depth; break;
      case 2: bps = 3 * depth; break;
      case 4: bps = depth<<1; break;
      case 6: bps = depth<<2; break;
      default: throw new IOException("Unknown color type encountered.");
    }

    filterOffset = (bps + 7) >> 3;

    for (pass = (multipass ? 1 : 0); pass < 8; pass++) {
        int pass = this.pass;
        int rInc = rowInc[pass];
        int cInc = colInc[pass];
        int sCol = startingCol[pass];
        int val = (width - sCol + cInc - 1) / cInc;
        int samples = val * filterOffset;
        int rowSize = (val * bps)>>3;
        int sRow = startingRow[pass];
        if (height <= sRow || rowSize == 0) continue;
        int sInc = rInc * width;
        byte inbuf[] = new byte[rowSize];
        int pix[] = new int[rowSize];
        int upix[] = null;
        int temp[] = new int[rowSize];
        int nextY = sRow;               // next Y value and number of rows to report to sendPixels
        int rows = 0;
        int rowStart = sRow * width;

        for (int y = sRow; y < height; y += rInc, rowStart += sInc) {
            rows += rInc;
            int rowFilter = dis.read();
            dis.readFully(inbuf);
            if (!filterRow(inbuf, pix, upix, rowFilter, filterOffset)) throw new IOException("Unknown filter type: " + rowFilter);
            insertPixels(pix, rowStart + sCol, samples);
            if (multipass && (pass < 6)) blockFill(rowStart);
            upix = pix;
            pix = temp;
            temp = upix;
        }
        if (!multipass) break;
    }
    while(dis.read() != -1) System.err.println("Leftover data encountered.");

    // 24-bit color is our native format
    if (colorType == 2 || colorType == 6) {
        data = (int[])pixels;
        if (colorType == 2) {
            for(int i=0; i<data.length; i++)
                data[i] |= 0xFF000000;
        }

    } else if (colorType == 3) {
        byte[] pix = (byte[])pixels;
        data = new int[pix.length];
        for(int i=0; i<pix.length; i++) {
            if (transparency) {
                data[i] =
                    ((palette[4 * (pix[i] & 0xff) + 3] & 0xff) << 24)|
                    ((palette[4 * (pix[i] & 0xff) + 0] & 0xff) << 16)|
                    ((palette[4 * (pix[i] & 0xff) + 1] & 0xff) << 8) |
                    (palette[4 * (pix[i] & 0xff) + 2] & 0xff);
            } else {
                data[i] =
                    0xFF000000 |
                    ((palette[3 * (pix[i] & 0xff) + 0] & 0xff) << 16)|
                    ((palette[3 * (pix[i] & 0xff) + 1] & 0xff) << 8) |
                    (palette[3 * (pix[i] & 0xff) + 2] & 0xff);
            }
        }

    } else if (colorType == 0 || colorType == 4) {
        if (depth == 16) depth = 8;
        int[] pix = (int[])pixels;
        data = new int[pix.length];
        for(int i=0; i<pix.length; i ++) {
            if (colorType == 0) {
                int val = (pix[i] & 0xff) << (8 - depth);
                data[i] =
                    0xFF000000 |
                    (val << 16) |
                    (val << 8) |
                    val;
            } else {
                int alpha = (pix[i] & mask) << (8 - depth);
                int val = ((pix[i] & smask) >> depth) << (8 - depth);
                data[i] =
                    (alpha << 24) |
                    (val << 16) |
                    (val << 8) |
                    val;
            }
        }
    }
  }

  private void insertGreyPixels(int pix[], int offset, int samples) {
    int p = pix[0];
    int ipix[] = ipixels;
    int cInc = colInc[pass];
    int rs = 0;

    if (colorType == 0) {
        switch (depth) {
        case 1:
            for (int j = 0; j < samples; j++, offset += cInc) {
                if (rs != 0) rs--;
                else { rs = 7; p = pix[j>>3]; }
                ipix[offset] = (p>>rs) & 0x1;
            }
            break;
        case 2:
            for (int j = 0; j < samples; j++, offset += cInc) {
                if (rs != 0) rs -= 2;
                else { rs = 6; p = pix[j>>2]; }
                ipix[offset] = (p>>rs) & 0x3;
            }
            break;
        case 4:
            for (int j = 0; j < samples; j++, offset += cInc) {
                if (rs != 0) rs = 0;
                else { rs = 4; p = pix[j>>1]; }
                ipix[offset] = (p>>rs) & 0xf;
            }
            break;
        case 8:
            for (int j = 0; j < samples; offset += cInc) ipix[offset]= (byte) pix[j++];
            break;
        case 16:
            samples = samples<<1;
            for (int j = 0; j < samples; j += 2, offset += cInc) ipix[offset] = pix[j];
            break;
        default: break;
        }
    } else if (colorType == 4) {
        if (depth == 8) {
            for (int j = 0; j < samples; offset += cInc) ipix[offset]= (pix[j++]<<8) | pix[j++];
        } else {
            samples = samples<<1;
            for (int j = 0; j < samples; j += 2, offset += cInc) ipix[offset] = (pix[j]<<8) | pix[j+=2];
        }
    }
  }

  private void insertPalettedPixels(int pix[], int offset, int samples){
    int rs = 0;
    int p = pix[0];
    byte bpix[] = bpixels;
    int cInc = colInc[pass];

    switch (depth) {
      case 1:
        for (int j = 0; j < samples; j++, offset += cInc) {
            if (rs != 0) rs--;
            else { rs = 7; p = pix[j>>3]; }
            bpix[offset] = (byte) ((p>>rs) & 0x1);
        }
        break;
      case 2:
        for (int j = 0; j < samples; j++, offset += cInc) {
            if (rs != 0) rs -= 2;
            else { rs = 6; p = pix[j>>2]; }
            bpix[offset] = (byte) ((p>>rs) & 0x3);
        }
        break;
      case 4:
        for (int j = 0; j < samples; j++, offset += cInc) {
            if (rs != 0) rs = 0;
            else { rs = 4; p = pix[j>>1]; }
            bpix[offset] = (byte) ((p>>rs) & 0xf);
        }
        break;
      case 8:
        for (int j = 0; j < samples; j++, offset += cInc) bpix[offset] = (byte) pix[j];
        break;
    }
  }

  private void insertPixels(int pix[], int offset, int samples) {
    switch (colorType) {
    case 0:
    case 4:
        insertGreyPixels(pix, offset, samples);
        break;
    case 2: {
        int j = 0;
        int ipix[] = ipixels;
        int cInc = colInc[pass];
        if (depth == 8) {
            for (j = 0; j < samples; offset += cInc)
                ipix[offset] = (pix[j++]<<16) | (pix[j++]<<8) | pix[j++];
        } else {
            samples = samples<<1;
            for (j = 0; j < samples; j += 2, offset += cInc)
                ipix[offset] = (pix[j]<<16) | (pix[j+=2]<<8) | pix[j+=2];
        }
        break; }
    case 3:
        insertPalettedPixels(pix, offset, samples);
        break;
    case 6: {
        int j = 0;
        int ipix[] = ipixels;
        int cInc = colInc[pass];
        if (depth == 8) {
            for (j = 0; j < samples; offset += cInc) {
                ipix[offset] = (pix[j++]<<16) | (pix[j++]<<8) | pix[j++] |
                                (pix[j++]<<24);
            }
        } else {
            samples = samples<<1;
            for (j = 0; j < samples; j += 2, offset += cInc) {
                ipix[offset] = (pix[j]<<16) | (pix[j+=2]<<8) | pix[j+=2] |
                                (pix[j+=2]<<24);
            }
        }
        break; }
      default:
        break;
    }
  }

  private void blockFill(int rowStart) {
    int counter;
    int dw = width;
    int pass = this.pass;
    int w = blockWidth[pass];
    int sCol = startingCol[pass];
    int cInc = colInc[pass];
    int wInc = cInc - w;
    int maxW = rowStart + dw - w;
    int len;
    int h = blockHeight[pass];
    int maxH = rowStart + (dw * h);
    int startPos = rowStart + sCol;
    counter = startPos;

    if (colorType == 3) {
        byte bpix[] = bpixels;
        byte pixel;
        len = bpix.length;
        for (; counter <= maxW;) {
            int end = counter + w;
            pixel = bpix[counter++];
            for (; counter < end; counter++) bpix[counter] = pixel;
            counter += wInc;
        }
        maxW += w;
        if (counter < maxW)
            for (pixel = bpix[counter++]; counter < maxW; counter++)
                bpix[counter] = pixel;
        if (len < maxH) maxH = len;
        for (counter = startPos + dw; counter < maxH; counter += dw)
            System.arraycopy(bpix, startPos, bpix, counter, dw - sCol);
    } else {
        int ipix[] = ipixels;
        int pixel;
        len = ipix.length;
        for (; counter <= maxW;) {
            int end = counter + w;
            pixel = ipix[counter++];
            for (; counter < end; counter++)
                ipix[counter] = pixel;
            counter += wInc;
        }
        maxW += w;
        if (counter < maxW)
            for (pixel = ipix[counter++]; counter < maxW; counter++)
                ipix[counter] = pixel;
        if (len < maxH) maxH = len;
        for (counter = startPos + dw; counter < maxH; counter += dw)
            System.arraycopy(ipix, startPos, ipix, counter, dw - sCol);
    }
  }

  private boolean filterRow(byte inbuf[], int pix[], int upix[], int rowFilter, int boff) {
    int rowWidth = pix.length;
    switch (rowFilter) {
    case 0: {
        for (int x = 0; x < rowWidth; x++) pix[x] = 0xff & inbuf[x];
        break; }
    case 1: {
        int x = 0;
        for ( ; x < boff; x++) pix[x] = 0xff & inbuf[x];
        for ( ; x < rowWidth; x++) pix[x] = 0xff & (inbuf[x] + pix[x - boff]);
        break; }
    case 2: {
        if (upix != null) {
            for (int x = 0; x < rowWidth; x++)
                pix[x] = 0xff & (upix[x] + inbuf[x]);
        } else {
            for (int x = 0; x < rowWidth; x++)
                pix[x] = 0xff & inbuf[x];
        }
        break; }
    case 3: {
        if (upix != null) {
            int x = 0;
            for ( ; x < boff; x++) {
                int rval = upix[x];
                pix[x] = 0xff & ((rval>>1) + inbuf[x]);
            }
            for ( ; x < rowWidth; x++) {
                int rval = upix[x] + pix[x - boff];
                pix[x] = 0xff & ((rval>>1) + inbuf[x]);
            }
        } else {
            int x = 0;
            for ( ; x < boff; x++) pix[x] = 0xff & inbuf[x];
            for ( ; x < rowWidth; x++) {
                int rval = pix[x - boff];
                pix[x] = 0xff & ((rval>>1) + inbuf[x]);
            }
        }
        break; }
    case 4: {
        if (upix != null) {
            int x = 0;
            for ( ; x < boff; x++) pix[x] = 0xff & (upix[x] + inbuf[x]);
            for ( ; x < rowWidth; x++) {
                int a,b,c,p,pa,pb,pc, rval;
                a = pix[x - boff];
                b = upix[x];
                c = upix[x - boff];
                p = a + b - c;
                pa = p > a ? p - a : a - p;
                pb = p > b ? p - b : b - p;
                pc = p > c ? p - c : c - p;
                if ((pa <= pb) && (pa <= pc)) rval = a;
                else if (pb <= pc) rval = b;
                else rval = c;
                pix[x] = 0xff & (rval + inbuf[x]);
            }
        } else {
            int x = 0;
            for ( ; x < boff; x++) pix[x] = 0xff & inbuf[x];
            for ( ; x < rowWidth; x++) {
                int rval = pix[x - boff];
                pix[x] = 0xff & (rval + inbuf[x]);
            }
        }
        break; }
    default: return false;
    }
    return true;
  }

  // Private Data ///////////////////////////////////////////////////////////////////////////////////////

  private int target_offset = 0;
  private int width = -1;
  private int height = -1;
  private int sigmask = 0xffff;
  private Object pixels = null;
  private int ipixels[] = null;
  private byte bpixels[] = null;
  private boolean multipass = false;
  private boolean complete = false;
  private boolean error = false;

  private int[] data = null;

  private InputStream underlyingStream = null;
  private DataInputStream inputStream = null;
  private Thread controlThread = null;
  private boolean infoAvailable = false;
  private int updateDelay = 750;

  // Image decoding state variables
  private boolean headerFound = false;
  private int compressionMethod = -1;
  private int depth = -1;
  private int colorType = -1;
  private int filterMethod = -1;
  private int interlaceMethod = -1;
  private int pass = 0;
  private byte palette[] = null;
  private int mask = 0x0;
  private int smask = 0x0;
  private boolean transparency = false;

  private int chunkLength = 0;
  private int chunkType = 0;
  private boolean needChunkInfo = true;

  private static final int CHUNK_bKGD = 0x624B4744;   // "bKGD"
  private static final int CHUNK_cHRM = 0x6348524D;   // "cHRM"
  private static final int CHUNK_gAMA = 0x67414D41;   // "gAMA"
  private static final int CHUNK_hIST = 0x68495354;   // "hIST"
  private static final int CHUNK_IDAT = 0x49444154;   // "IDAT"
  private static final int CHUNK_IEND = 0x49454E44;   // "IEND"
  private static final int CHUNK_IHDR = 0x49484452;   // "IHDR"
  private static final int CHUNK_PLTE = 0x504C5445;   // "PLTE"
  private static final int CHUNK_pHYs = 0x70485973;   // "pHYs"
  private static final int CHUNK_sBIT = 0x73424954;   // "sBIT"
  private static final int CHUNK_tEXt = 0x74455874;   // "tEXt"
  private static final int CHUNK_tIME = 0x74494D45;   // "tIME"
  private static final int CHUNK_tRNS = 0x74524E53;   // "tIME"
  private static final int CHUNK_zTXt = 0x7A545874;   // "zTXt"

  private static final int startingRow[]  =  { 0, 0, 0, 4, 0, 2, 0, 1 };
  private static final int startingCol[]  =  { 0, 0, 4, 0, 2, 0, 1, 0 };
  private static final int rowInc[]       =  { 1, 8, 8, 8, 4, 4, 2, 2 };
  private static final int colInc[]       =  { 1, 8, 8, 4, 4, 2, 2, 1 };
  private static final int blockHeight[]  =  { 1, 8, 8, 4, 4, 2, 2, 1 };
  private static final int blockWidth[]   =  { 1, 8, 4, 4, 2, 2, 1, 1 };

  // Helper Classes ////////////////////////////////////////////////////////////////////

  private static class MeteredInputStream extends FilterInputStream {
    int bytesLeft;
    int marked;

  public MeteredInputStream(InputStream in, int size) {
    super(in);
    bytesLeft = size;
  }

  public final int read() throws IOException {
    if (bytesLeft > 0) {
        int val = in.read();
        if (val != -1) bytesLeft--;
        return val;
    }
    return -1;
  }

  public final int read(byte b[]) throws IOException {
    return read(b, 0, b.length);
  }

  public final int read(byte b[], int off, int len) throws IOException {
    if (bytesLeft > 0) {
      len = (len > bytesLeft ? bytesLeft : len);
      int read = in.read(b, off, len);
      if (read > 0) bytesLeft -= read;
      return read;
    }
    return -1;
  }

  public final long skip(long n) throws IOException {
    n = (n > bytesLeft ? bytesLeft : n);
    long skipped = in.skip(n);
    if (skipped > 0) bytesLeft -= skipped;
    return skipped;
  }

  public final int available() throws IOException {
    int n = in.available();
    return (n > bytesLeft ? bytesLeft : n);
  }

  public final void close() throws IOException { /* Eat this */ }

  public final void mark(int readlimit) {
    marked = bytesLeft;
    in.mark(readlimit);
  }

  public final void reset() throws IOException {
    in.reset();
    bytesLeft = marked;
  }

  public final boolean markSupported() {
    return in.markSupported();
  }
}

  /** Support class, used to eat the IDAT headers dividing up the deflated stream */
  private static class IDATEnumeration implements Enumeration {
    InputStream underlyingStream;
    PngDecoder owner;
    boolean firstStream = true;

    public IDATEnumeration(PngDecoder owner) {
      this.owner = owner;
      this.underlyingStream = owner.underlyingStream;
    }

    public Object nextElement() {
      firstStream = false;
      return new MeteredInputStream(underlyingStream, owner.chunkLength);
    }

    public boolean hasMoreElements() {
      DataInputStream dis = new DataInputStream(underlyingStream);
      if (!firstStream) {
        try {
          int crc = dis.readInt();
          owner.needChunkInfo = false;
          owner.chunkLength = dis.readInt();
          owner.chunkType = dis.readInt();
        } catch (IOException ioe) {
          return false;
        }
      }
      if (owner.chunkType == PngDecoder.CHUNK_IDAT) return true;
      return false;
    }
  }
}
