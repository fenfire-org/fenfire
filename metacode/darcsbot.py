# Twisted, the Framework of Your Internet
# Copyright (C) 2001 Matthew W. Lefkowitz
#               2003-2004 Tuukka Hastrup and others?
#               2004-2005 Matti J. Katila
#
# This library is free software; you can redistribute it and/or
# modify it under the terms of version 2.1 of the GNU Lesser General Public
# License as published by the Free Software Foundation.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

from twisted.internet.protocol import Protocol, Factory
from twisted.protocols import irc
from twisted.internet import reactor, protocol
from twisted.internet.app import Application

from twisted.python.rebuild import rebuild

import sys
import time
import os.path
from traceback import print_exc


import darcsbot

MASTER_HOSTS = ['siksak.it.jyu.fi']
WATCH_DELAY = 15
LINE_DELAY = 5

# IRC-options
CHANNEL = "#fenfire"
NICK = "ffdarcsbot"
IRC_SERVER = "irc.freenode.net"

# Project options
projects = [ 'libvob', 'callgl', 'fenfire', 'storm', 'alph', 'depends', 'ff', 'navidoc' ]
SENT_DIR = 'darcsbot_sent_commits'

if not os.path.exists(SENT_DIR):
    os.mkdir(SENT_DIR)


class Bot(irc.IRCClient):
    """An IRC bot."""
    def __init__(self, nick):
        self.nickname = nick
        print 'asdf'

    def signedOn(self):
        self.join(self.factory.channel)
        self.darcswatch()

    def joined(self, channel):
        if channel == self.factory.channel:
            print 'joined', channel
            self.msg(channel, 'hippaladuida')

    def privmsg(self, user, channel, message):
        print 'privmsg', user, channel, message
        username = user[:user.index('!')]
        host = user[user.rindex('@')+1:]
        if channel.startswith('#'):
            dest = channel
        else: dest = username

        if message == 'rebuild':
            try:
                rebuild(darcsbot)
                print 'rebuilt'
            except:
                print_exc()
            return

    def noticed(self, user, channel, message):
        pass # override needed, because default is to call privmsg

    def darcswatch(self, firsttime = 0):
        try:
            def send_commit(msg):
                self.notice(self.factory.channel, msg)
            for p in projects:
                project(p, send_commit)
            time.sleep(LINE_DELAY)
        except:
            print_exc()

        reactor.callLater(WATCH_DELAY, self.darcswatch)


class BotFactory(protocol.ClientFactory):
    """A factory for LogBots.

    A new protocol instance will be created each time we connect to the server.
    """

    # the class of the protocol to build
    protocol = Bot
    instance = None   

    def buildProtocol(self, addr):
        p = self.protocol(self.nick)
        p.factory = self
        self.instance = p 
        return p

    def __init__(self, botnick, channel):
        self.nick = botnick
        self.channel = channel
 
    def clientConnectionLost(self, connector, reason):
        """If we get disconnected, reconnect to server."""
        connector.connect()

    def clientConnectionFailed(self, connector, reason):
        print "connection failed:", reason
        reactor.stop()




def send(proj, p, send_notice):
    x=os.system("darcs changes --match 'hash %s' --repo=%s " % (p, proj) +
                "> "+SENT_DIR+"/desc")
    y=os.system("darcs diff -u --match 'hash %s' --repo=%s " % (p, proj) +
                "> "+SENT_DIR+"/diff")

    if x>0 or y>0:
        print '-- skip %s: darcs exited with status code %s/%s --' % (p,x,y)
        return 0

    lines = file(SENT_DIR+'/desc', 'r').readlines()

    first_line = lines[0][:-1]
    title = lines[1][:-1].strip()
    desc = '\n'.join([l.strip() for l in lines[2:]]).strip()

    title = title[2:] # strip off '* '

    if title.find('Initial commit') >= 0 and title.find('darcs-import') >= 0:
        # initial commit -- skip!
        return 1

    tokens = [tok for tok in first_line.split(' ') if tok != '']

    date = ' '.join(tokens[:6])
    author = ' '.join(tokens[6:])

    print
    print 'Title:', title
    print 'Author:', author
    print

    #print 'Date:', date

    #if desc:
    #    print
    #    print desc

    if not author:
        # no author set
        author = 'unknown author'

    try:
        send_notice(proj+': '+author+', '+title)
        return 1
    finally:
        os.system('rm '+SENT_DIR+'/desc')
        os.system('rm '+SENT_DIR+'/diff')

    return 0



def project(proj, send_notice):
    if not os.path.isdir(proj):
        # we don't have that project -- skip
        return
    
    FILE = SENT_DIR+'/%s-patches-sent' % proj

    os.system('touch '+FILE)

    patches_sent = [l[:-1] for l in open(FILE, 'r').xreadlines()]
    patches = [s for s in os.listdir('%s/_darcs/patches/' % proj)
                 if s.endswith('.gz')]
    
    patches.sort()
    
    to_be_sent = [p for p in patches if p not in patches_sent]
    has_sent = []

    for p in to_be_sent:
        if send(proj, p, send_notice): has_sent.append(p)

    f = open(FILE, 'a')
    for p in has_sent: f.write(p+'\n')




if __name__ == '__main__':
    from twisted.internet.app import Application

    import darcsbot

    try:
        channel = sys.argv[1]
    except:
        channel = CHANNEL

    botf = darcsbot.BotFactory(NICK, channel)

    app = Application("darcsbot")

    app.connectTCP(IRC_SERVER, 6667, botf)
    app.run()
