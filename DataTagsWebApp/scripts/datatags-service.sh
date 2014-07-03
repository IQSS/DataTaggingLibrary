#!/bin/sh
#
# TaggingServer Startup script for the tagging server of the DataTags Project
#
# chkconfig:   - 20 80
# description: Runs the tagging server, a play framework application\
#              Author: Michael Bar-Sinai (mbarsinai@iq.harvard.edu)

### BEGIN INIT INFO
# Provides: datatagsTaggingServer
# Required-Start: httpd
# Short-Description: DataTags Tagging Server
# Description:      DataTags Tagging Server allows users to tag their datasets (see www.datatags.org)
### END INIT INFO

# Source function library.
. /etc/rc.d/init.d/functions

rundir="path-to-top-play-dir"
exec="/path/to/<daemonname>"
prog="TaggingServer"
config="<path to major config file>"

[ -e /etc/sysconfig/$prog ] && . /etc/sysconfig/$prog

lockfile=/var/lock/subsys/$prog

start() {
    [ -x $exec ] || exit 5
    [ -f $config ] || exit 6
    echo -n $"Starting $prog: "
    
    cd $rundir
    daemon $exec
    
    retval=$?
    echo
    [ $retval -eq 0 ] && touch $lockfile
    return $retval
}

stop() {
    echo -n $"Stopping $prog: "
    killproc $prog
    retval=$?
    echo
    [ $retval -eq 0 ] && rm -f $lockfile
    return $retval
}

restart() {
    stop
    start
}

reload() {
    restart
}

force_reload() {
    restart
}

rh_status() {
    # run checks to determine if the service is running or use generic status
    status $prog
}

rh_status_q() {
    rh_status >/dev/null 2>&1
}


case "$1" in
    start)
        rh_status_q && exit 0
        $1
        ;;
    stop)
        rh_status_q || exit 0
        $1
        ;;
    restart)
        $1
        ;;
    reload)
        rh_status_q || exit 7
        $1
        ;;
    force-reload)
        force_reload
        ;;
    status)
        rh_status
        ;;
    condrestart|try-restart)
        rh_status_q || exit 0
        restart
        ;;
    *)
        echo $"Usage: $0 {start|stop|status|restart|condrestart|try-restart|reload|force-reload}"
        exit 2
esac
exit $?