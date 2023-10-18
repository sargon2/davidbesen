#include <fcntl.h>
#include <ncp/nwcalls.h>
#include "private/libintl.h"

#define _(X) gettext(X)

// extremely rough program to demonstrate setting flags
// segfaults if you give it a glob or a file not on a netware volume
// the actual mode is '160', which expands to rwsha

int main(int argc, char *const argv[])
{

	int fd;
	struct NWCCRootEntry entry;
	NWCCODE err;
	struct ncp_dos_info nwmd;
	u_int32_t mask = 0;
	NWCONN_HANDLE con;
	if(argc < 2) {
		printf("usage: %s <file>\n", argv[0]);
		return 0;
	}
	setlocale(LC_ALL, "");
	bindtextdomain(NCPFS_PACKAGE, LOCALEDIR);
	textdomain(NCPFS_PACKAGE);
	fd = open(argv[1], O_RDONLY);
	ncp_open_fd(fd, &con);
	NWCCGetConnInfo(con, NWCC_INFO_ROOT_ENTRY, sizeof(entry), &entry);
	mask |= DM_ATTRIBUTES;
	memset(&nwmd, 0, sizeof(nwmd));
	nwmd.Attributes = 160;
	err = ncp_ns_modify_entry_dos_info(
		con, NW_NS_DOS, SA_ALL, 
		1, entry.volume, entry.dirEnt, NULL, 0,
		mask, &nwmd);
	if (err) {
		fprintf(stderr, _("Cannot set file attributes on %s because of %s\n"),
			argv[1],
			strnwerror(err));
	}
	if (con)
		ncp_close(con);
	return 0;
}
