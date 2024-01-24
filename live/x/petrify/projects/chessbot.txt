#!/usr/bin/perl -w

use Net::IRC;
use strict;

my %boards;
my %players; # who's white and who's black
my %games; # locate opponent
my %turn; # whose turn is it
my %enpassant; #last double-moved pawn
my %error; #last error message

my $chan;
$chan = "#test";

my $irc = new Net::IRC;

my $conn = $irc->newconn(Nick => 'chessBot', Server=>'bigfeh.com', Ircname=>'chessBot');

$conn->add_global_handler('376', \&on_connect);
$conn->add_global_handler('public', \&on_public);
$conn->add_handler('msg', \&on_msg);

print ("....\n");
$irc->start;

sub on_connect {
    my $self = shift;
    $self->join ($chan);
    $self->print ("joined\n");
}

sub on_msg {
    my ($self, $event) = @_;
    my ($arg) = $event->args;
    $arg=lc($arg);
    my $nick = $event->nick;
    $nick=lc($nick);
    my @in = split(/ /, $arg);
    if($in[0] eq "join") {
        $self->join($chan);
        return;
    }
    if($in[0] eq "chal") {
        if($nick eq $in[1]) {
            $self->privmsg($nick, "cannot challenge yourself");
            return;
        }
        initboard($nick, $in[1]);
        $self->privmsg($nick, "Sent challenge to $in[1]");
        $self->privmsg($in[1], "You have been challenged by $nick");
        $players{$nick} = "white";
        $players{$in[1]} = "black";
        $games{$nick} = $in[1];
        $games{$in[1]} = $nick;
        $turn{$nick} = "1";
        return;
    }
    if($in[0] eq "draw") {
        if($boards{$nick}) {
            drawboard($self, $nick);
        } else {
            $self->privmsg($nick, "sorry, you're not in a match");
        }
        return;
    }
    $in[0] = lc($in[0]);
    if($boards{$nick}) {
        if($turn{$nick}) {
            if($enpassant{$nick}) {delete $enpassant{$nick};}
            if(islegal($in[0], $nick)==1) { # keep this innermost because it can delete pieces (fixed)
                move($in[0], $nick);
                if($error{$nick} =~ /^delete/) { # en passant
                    eval $error{$nick};
                }
                #drawboard($self, $nick);
                #drawboard($self, $games{$nick});
                delete $turn{$nick};
                $turn{$games{$nick}} = "1";
            } else {
                $self->privmsg($nick, "Invalid move $in[0]: $error{$nick}");
            }
        } else {
            $self->privmsg($nick, "not your turn");
        }
    } else {
        $self->privmsg($nick, "sorry, you're not in a match");
    }
}

sub on_public {
    my ($self, $event) = @_;
    my ($arg) = $event->args;
}

sub initboard {
    my ($who, $who2) = @_;
    my $i;
    my $tempboard = {}; # hash ref so $boards{} will be the same data for both people
    $boards{$who} = $tempboard; $boards{$who2} = $tempboard;
    $boards{$who}{"1:1"} = "WRook";
    $boards{$who}{"2:1"} = "WKnight";
    $boards{$who}{"3:1"} = "WBishop";
    $boards{$who}{"4:1"} = "WQueen";
    $boards{$who}{"5:1"} = "WKing";
    $boards{$who}{"6:1"} = "WBishop";
    $boards{$who}{"7:1"} = "WKnight";
    $boards{$who}{"8:1"} = "WRook";
    for ($i = 1; $i < 9; $i++) {
        $boards{$who}{"$i:2"} = "WPawn";
    }
    $boards{$who}{"1:8"} = "BRook";
    $boards{$who}{"2:8"} = "BKnight";
    $boards{$who}{"3:8"} = "BBishop";
    $boards{$who}{"4:8"} = "BQueen";
    $boards{$who}{"5:8"} = "BKing";
    $boards{$who}{"6:8"} = "BBishop";
    $boards{$who}{"7:8"} = "BKnight";
    $boards{$who}{"8:8"} = "BRook";
    for ($i = 1; $i < 9; $i++) {
        $boards{$who}{"$i:7"} = "BPawn";
    }
}

sub drawboard {
    my ($self, $who) = @_;
    my @colors;
    my $tmp = 0;
    my $outline;
    my ($x, $y, $tx, $ty);
    $colors[0] = "4,0";  # BW -- piece, board
    $colors[1] = "4,1";  # BB
    $colors[2] = "1,0";  # WW
    $colors[3] = "0,1";  # WB
    if($players{$who} eq "white") {$self->privmsg($who, "   A B C D E F G H");} else {$self->privmsg($who, "   H G F E D C B A");}
    my $wback = 0;

    for($ty = 1; $ty < 9; $ty++) {
        if($players{$who} eq "white") { $y = 9 - $ty;} else {$y = $ty;}
        $outline = "$y ";
        for($tx = 8; $tx > 0; $tx--) {
            if($players{$who} eq "white") { $x = 9 - $tx;} else {$x = $tx;}
            $tmp = 0;
            if($wback==1) {
                $tmp += 1;
                $wback = 0;
            } else {
                $wback = 1;
            }
            if(!defined $boards{$who}{"$x:$y"}) {
                $outline .= "".$colors[$tmp]."  ";
            } else {
                if(substr($boards{$who}{"$x:$y"}, 0, 1) eq "W") {$tmp += 2;}
                if($boards{$who}{"$x:$y"}) {
                    $outline .= "".$colors[$tmp].(substr($boards{$who}{"$x:$y"}, 1, 2));
                }
            }
        }
        $outline .= " $y";
        $self->privmsg($who, $outline);
        if($wback==1) {$wback = 0;} else {$wback = 1;}
    }
    if($players{$who} eq "white") {$self->privmsg($who, "   A B C D E F G H");} else {$self->privmsg($who, "   H G F E D C B A");}
}

sub move {
    my ($where, $nick) = @_;
    my $fromx = substr($where, 0, 1);
    $fromx =~ tr/a-h/1-8/;
    my $fromy = substr($where, 1, 1);
    my $tox = substr($where, 2, 1);
    $tox =~ tr/a-h/1-8/;
    my $toy = substr($where, 3, 1);
    $boards{$nick}{"$tox:$toy"} = $boards{$nick}{"$fromx:$fromy"};
    delete $boards{$nick}{"$fromx:$fromy"};
}

sub islegal { # no longer deletes peices, so it's safe to call this from other functions
    my ($where, $nick) = @_;
    my $fromx = substr($where, 0, 1);
    $fromx =~ tr/a-h/1-8/;
    my $fromy = substr($where, 1, 1);
    my $tox = substr($where, 2, 1);
    $tox =~ tr/a-h/1-8/;
    my $toy = substr($where, 3, 1);
    delete $error{$nick};

    if(!defined $boards{$nick}{"$fromx:$fromy"}) {
        $error{$nick} = "Piece does not exist";
        return 0;
    }

    if(($tox == $fromx)&&($toy == $fromy)) {
        $error{$nick} = "try again";
        return 0;
    }

    if($tox < 1) {$error{$nick} = "cannot move off board"; return 0;}
    if($tox > 8) {$error{$nick} = "cannot move off board"; return 0;}
    if($toy < 1) {$error{$nick} = "cannot move off board"; return 0;}
    if($toy > 8) {$error{$nick} = "cannot move off board"; return 0;}

    if($players{$nick} eq "white") {
        if(substr($boards{$nick}{"$fromx:$fromy"}, 0, 1) ne "W") {
            $error{$nick} = "try moving your own pieces";
            return 0;
        }
        if($boards{$nick}{"$tox:$toy"}) {
            if(substr($boards{$nick}{"$tox:$toy"}, 0, 1) eq "W") {
                $error{$nick} = "you cannot take your own pieces";
                return 0;
            }
        }
    } else {
        if(substr($boards{$nick}{"$fromx:$fromy"}, 0, 1) ne "B") {
            $error{$nick} = "try moving your own pieces";
            return 0;
        }
        if($boards{$nick}{"$tox:$toy"}) {
            if(substr($boards{$nick}{"$tox:$toy"}, 0, 1) eq "B") {
                $error{$nick} = "you cannot take your own pieces";
                return 0;
            }
        }
    }

    my $tmpstr;
    my ($maxx, $maxy, $minx, $miny);
    my $i;
    my ($xincr, $yincr);

    if(substr($boards{$nick}{"$fromx:$fromy"}, 1) eq "Pawn") { #############################################
        if(abs($fromy - $toy) == 2) {
            if($fromx != $tox) {$error{$nick} = "Pawn must stay in rank on first move"; return 0;}
            if($players{$nick} eq "white") {
                $tmpstr = $fromy+1;
                if($boards{$nick}{"$fromx:$tmpstr"}) {$error{$nick} = "Pawn cannot move through piece"; return 0;}
                $tmpstr = $fromy+2;
                if($boards{$nick}{"$fromx:$tmpstr"}) {$error{$nick} = "Pawn cannot move through piece"; return 0;}
                if($fromy == 2) {$enpassant{$nick} = $fromx; return 1;}
            } else {
                $tmpstr = $fromy-1;
                if($boards{$nick}{"$fromx:$tmpstr"}) {$error{$nick} = "Pawn cannot move through piece"; return 0;}
                $tmpstr = $fromy-2;
                if($boards{$nick}{"$fromx:$tmpstr"}) {$error{$nick} = "Pawn cannot move through piece"; return 0;}
                if($fromy == 7) {$enpassant{$nick} = $fromx; return 1;}
            }
            $error{$nick} = "Illegal pawn move";
            return 0;
        }
        if(abs($fromy - $toy) != 1) {
            $error{$nick} = "Pawn cannot move that far";
            return 0;
        }
        if($fromx == $tox) {
            if($boards{$nick}{"$tox:$toy"}) {
                $error{$nick} = "Pawn cannot move through piece"; return 0;
                return 0;
            } else {
                if($players{$nick} eq "white") {
                    if($fromy != $toy-1) {$error{$nick} = "Pawn must move one space forward"; return 0;}
                } else {
                    if($fromy != $toy+1) {$error{$nick} = "Pawn must move one space forward"; return 0;}
                }
                return 1;
            }
        } else {
            if(abs ($fromx - $tox) != 1) {
                $error{$nick} = "Pawns cannot move that far horizontally";
                return 0;
            }
            if(!defined $boards{$nick}{"$tox:$toy"}) {
                # en passant, or something?
                if($players{$nick} eq "white") {
                    if($fromy != 5) {$error{$nick} = "En passant must be from rank 5 for white"; return 0;}
                    if($boards{$nick}{"$tox:$fromy"}) {
                        if($boards{$nick}{"$tox:$fromy"} eq "BPawn") {
                            if(!defined $enpassant{$games{$nick}}) {$error{$nick} = "En passant must be done immediately after double-square pawn move"; return 0;}
                            if($enpassant{$games{$nick}} == $tox) {
                                #delete $boards{$nick}{"$tox:$fromy"}; #bad form to delete this here...
                                $error{$nick} = 'delete $boards{$nick}{"$tox:$fromy"};';
                                return 1;
                            }
                        }
                    }
                    $error{$nick} = "Pawns cannot do that";
                    return 0;
                } else {
                    if($fromy != 4) {$error{$nick} = "En passant must be from rank 4 for black"; return 0;}
                    if($boards{$nick}{"$tox:$fromy"}) {
                        if($boards{$nick}{"$tox:$fromy"} eq "WPawn") {
                            if(!defined $enpassant{$games{$nick}}) {$error{$nick} = "En passant must be done immediately after double-square pawn move"; return 0;}
                            if($enpassant{$games{$nick}} == $tox) {
                                #delete $boards{$nick}{"$tox:$fromy"}; #bad form to delete this here...
                                $error{$nick} = 'delete $boards{$nick}{"$tox:$fromy"};';
                                return 1;
                            }
                        }
                    }
                    $error{$nick} = "Pawns cannot do that";
                    return 0;
                }
                $error{$nick} = "Pawns cannot do that";
                return 0;
            }
            return 1;
        }
    } elsif(substr($boards{$nick}{"$fromx:$fromy"}, 1) eq "Rook") { ###############################################
        if(($fromx!=$tox)&&($fromy!=$toy)) {
            $error{$nick} = "Rooks don't move like that";
            return 0;
        }
        if($fromy==$toy) {
            $maxx = ($fromx>$tox)?$fromx:$tox;
            $maxx--;
            $minx = ($fromx<$tox)?$fromx:$tox;
            $minx++;
            for($i = $minx; $i <= $maxx; $i++) {
                if($boards{$nick}{"$i:$fromy"}) {
                    $error{$nick} = "Rooks cannot jump pieces";
                    return 0;
                }
            }
        } else {
            $maxy = ($fromy>$toy)?$fromy:$toy;
            $maxy--;
            $miny = ($fromy<$toy)?$fromy:$toy;
            $miny++;
            for($i = $miny; $i <= $maxy; $i++) {
                if($boards{$nick}{"$fromx:$i"}) {
                    $error{$nick} = "Rooks cannot jump pieces";
                    return 0;
                }
            }
        }
        return 1;
    } elsif(substr($boards{$nick}{"$fromx:$fromy"}, 1) eq "Knight") { #############################################
        if(abs($fromx - $tox) == 1) {
            if(abs($fromy - $toy) == 2) {
                return 1;
            }
        }
        if(abs($fromx - $tox) == 2) {
            if(abs($fromy - $toy) == 1) {
                return 1;
            }
        }
        $error{$nick} = "Knights cannot move like that";
        return 0;
    } elsif(substr($boards{$nick}{"$fromx:$fromy"}, 1) eq "Bishop") { #############################################
        if(abs($fromx - $tox) != abs($fromy - $toy)) {
            $error{$nick} = "Bishops don't move like that";
            return 0;
        }
        $xincr = ($fromx>$tox)?-1:1;
        $yincr = ($fromy>$toy)?-1:1;
        my ($x, $y);
        $y = $fromy+$yincr;
        for($x = $fromx+$xincr; $x != $tox; $x += $xincr) {
            if($boards{$nick}{"$x:$y"}) {
                $error{$nick} = "Bishops cannot jump pieces";
                return 0;
            }
            $y += $yincr;
        }
        return 1;
    } elsif(substr($boards{$nick}{"$fromx:$fromy"}, 1) eq "King") { ###############################################
        # castling...
        if($players{$nick} eq "white") {
            if(($fromy == 1)&&($fromx == 5)) {
            }
        } else {
            if(($fromy == 8)&&($fromx == 4)) {
            }
        }
        if(abs($fromx - $tox) > 1) {
            $error{$nick} = "Kings may only move one square";
            return 0;
        }
        if(abs($fromy - $toy) > 1) {
            $error{$nick} = "Kings may only move one square";
            return 0;
        }
        return 1;
    } elsif(substr($boards{$nick}{"$fromx:$fromy"}, 1) eq "Queen") { ##############################################
        if(($fromx!=$tox)&&($fromy!=$toy)) { # rookcheck
            if(abs($fromx - $tox) != abs($fromy - $toy)) { # bishopcheck
                $error{$nick} = "Queens don't move like that";
                return 0;
            }
        }
        if($fromx>$tox) {$xincr=-1;}
        if($fromx<$tox) {$xincr=1;}
        if($fromx==$tox) {$xincr=0;}
        if($fromy>$toy) {$yincr=-1;}
        if($fromy<$toy) {$yincr=1;}
        if($fromy==$toy) {$yincr=0;}
        my($x, $y);
        for($x = $fromx+$xincr; $x != $tox; $x += $xincr) {
            if($boards{$nick}{"$x:$y"}) {
                $error{$nick} = "Queens cannot jump pieces";
                return 0;
            }
            $y += $yincr;
        }
        return 1;
    }
    $error{$nick} = "Invalid piece located";
    return 0;
}
