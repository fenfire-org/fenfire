# (c): Benja Fallenstein and Matti J. Katila

"""
Program that sends a source diff to the mailing-list.

"""



import os, sys

dry_run = (len(sys.argv) > 1 and sys.argv[1] == '--dry-run')

TO       = 'Fenfire commit messages <fencommits@lists.jyu.fi>'
REPLY_TO = 'Fenfire developers list <fenfire-dev@nongnu.org>'

projects = [ 'libvob', 'callgl', 'fenfire', 'storm', 'alph', 'depends', 'ff', 'navidoc', '!private' ] # '!' signals to look in ~befallen/darcs/
LOCK = 'send_darcs_diffs/lock'

if not os.path.exists('send_darcs_diffs'):
    os.mkdir('send_darcs_diffs')

if os.path.isfile(LOCK):
    print 'exiting - a diffing process already running'
    sys.exit(42)
else:
    print 'continue'
    
os.system('touch '+LOCK)


def mail(projectName, fileName, author, title):
    print 'send:', projectName, fileName, author, title

    import smtplib

    msg = "From: %s\r\nTo: %s\r\nSubject: %s: %s\r\nReply-To: %s\r\n\r\n" \
	  % (author, TO, projectName, title, REPLY_TO)

    f = open(fileName, 'r')
    for s in f.xreadlines():
        msg += s
    f.close()

    if not dry_run:
        server = smtplib.SMTP('smtp.cc.jyu.fi')
        server.set_debuglevel(1)
        server.sendmail(author, TO, msg)
        server.quit()
    else:
        print 'would have sent this message:'
        print msg
        print


def send(_proj, p):
    if _proj[0] != '!':
        proj = _proj
        x=os.system("darcs changes --match 'hash %s' --repo=%s " % (p, proj) +
                    "> send_darcs_diffs/desc")
        y=os.system("darcs diff -u --match 'hash %s' --repo=%s " % (p, proj) +
                    "> send_darcs_diffs/diff")
    else:
        proj = _proj[1:]
        pdir = '/home/b/befallen/darcs/%s' % proj
        x=os.system("darcs changes --match 'hash %s' --repo=%s " % (p, pdir) +
                    "> send_darcs_diffs/desc")
        y=0
        f = open('send_darcs_diffs/desc')
        msg = f.read();
        f.close()
        
        f = open('send_darcs_diffs/diff', 'w')
        f.write(msg);
        f.write("\nThis is a patch to the 'private' archive, \n"
                "which contains code we can currently not publish\n"
                "due to legal issues. For this reason, this mail\n"
                "does not include a source code diff. (Sorry.)\n")
        f.close()

    if x>0 or y>0:
        print '-- skip %s: darcs exited with status code %s/%s --' % (p,x,y)
        return 0

    lines = file('send_darcs_diffs/desc', 'r').readlines()

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
        author = 'fencommits@lists.jyu.fi'

    try:
        mail(proj, 'send_darcs_diffs/diff', author, title)
        return 1
    finally:
        os.system('rm send_darcs_diffs/desc')
        os.system('rm send_darcs_diffs/diff')

    return 0



def project(_proj):
    if _proj[0] != '!':
        proj = pdir = _proj
    else:
        proj = _proj[1:]
        pdir = '/home/b/befallen/darcs/%s' % proj

    if not os.path.isdir(pdir):
        # we don't have that project -- skip
        return
    
    FILE = 'send_darcs_diffs/%s-patches-sent' % proj

    os.system('touch '+FILE)

    patches_sent = [l[:-1] for l in open(FILE, 'r').xreadlines()]
    patches = [s for s in os.listdir('%s/_darcs/patches/' % pdir)
                 if s.endswith('.gz')]
    
    patches.sort()
    
    to_be_sent = [p for p in patches if p not in patches_sent]
    has_sent = []

    for p in to_be_sent:
        if send(_proj, p): has_sent.append(p)

    if not dry_run:
        f = open(FILE, 'a')
        for p in has_sent: f.write(p+'\n')
        f.close()
    


try:
    for proj in projects: project(proj)
finally:
    os.system('rm '+LOCK)

