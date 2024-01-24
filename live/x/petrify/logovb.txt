Dim angle As Double ' in rads
Dim x As Double, y As Double, lastx As Double, lasty As Double
Dim pi As Double
Dim pendown As Boolean
Dim color As Long

Private Sub Form_Load()
    Dim i As Integer
    pi = 3.14159265358979
    Me.WindowState = vbMaximized
    Me.BackColor = vbWhite
    Me.AutoRedraw = True
    angle = 0
    x = 100
    y = 700
    color = vbBlack
    pendown = True
    Me.Visible = True
    DoEvents
    For i = 1 To 7
        Me.Cls
        angle = 0
        x = 200
        y = 650
        lt 90
        Hilbert i, 90, 256 / (2 ^ (i - 1))
        MsgBox "adsf"
    Next i
    End
End Sub

Private Sub Hilbert(l As Integer, a As Double, h As Double)
    If (l = 0) Then Exit Sub
    
    rt a
    Hilbert l - 1, -a, h
    fw h
    lt a
    Hilbert l - 1, a, h
    fw h
    Hilbert l - 1, a, h
    lt a
    fw h
    Hilbert l - 1, -a, h
    rt a

End Sub

Private Sub rt(deg As Double)
    angle = angle + (deg * (pi / 180))
'    If (angle > pi) Then angle = angle - (pi * 2)
End Sub

Private Sub lt(deg As Double)
    angle = angle - (deg * (pi / 180))
'    If (angle < 0) Then angle = angle + (pi * 2)
End Sub

Private Sub fw(dist As Double)
    lastx = x
    lasty = y
    x = x + dist * Cos(angle)
    y = y + dist * Sin(angle)
    If pendown Then Line (lastx, lasty)-(x, y), color
End Sub

Private Sub bk(dist As Double)
    lastx = x
    lasty = y
    x = x - dist * Cos(angle)
    y = y - dist * Sin(angle)
    If pendown Then Line (lastx, lasty)-(x, y), color
End Sub

