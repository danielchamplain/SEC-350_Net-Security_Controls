firewall {
    name DMZ-to-LAN {
        default-action drop
        enable-default-log
        rule 2 {
            action accept
            state {
                established enable
            }
        }
        rule 10 {
            action accept
            description "Allow Logger from DMZ to LAN UDP 1514"
            destination {
                port 1514
            }
            protocol udp
        }
    }
    name DMZ-to-WAN {
        default-action drop
        enable-default-log
        rule 2 {
            action accept
            state {
                established enable
            }
        }
        rule 10 {
            action accept
            description NTP
            destination {
                port 123
            }
            protocol udp
            source {
                address 172.16.50.3
            }
        }
    }
    name LAN-to-DMZ {
        default-action drop
        enable-default-log
        rule 20 {
            action accept
            description "Allow Web01 from WKS01"
            destination {
                port 80
            }
            protocol tcp
        }
        rule 25 {
            action accept
            description "SSH from MGMT to WEB01"
            destination {
                address 172.16.50.3
                port 22
            }
            protocol tcp
            source {
                address 172.16.200.11
            }
        }
    }
    name LAN-to-WAN {
        default-action drop
        enable-default-log
        rule 1 {
            action accept
        }
    }
    name WAN-to-DMZ {
        default-action drop
        enable-default-log
        rule 2 {
            action accept
            state {
                established enable
            }
        }
        rule 10 {
            action accept
            description "Allow WAN Access to web01 HTTP"
            destination {
                address 172.16.50.3
                port 80
            }
            protocol tcp
        }
    }
    name WAN-to-LAN {
        default-action drop
        enable-default-log
        rule 2 {
            action accept
            state {
                established enable
            }
        }
    }
}
interfaces {
    ethernet eth0 {
        address 10.0.17.149/24
        description SEC350-WAN
        hw-id 00:50:56:b3:f5:47
    }
    ethernet eth1 {
        address 172.16.50.2/29
        description SEC350-DMZ
        hw-id 00:50:56:b3:39:1e
    }
    ethernet eth2 {
        address 172.16.150.2/24
        description SEC350-LAN
        hw-id 00:50:56:b3:15:f5
    }
    loopback lo {
    }
}
nat {
    source {
        rule 10 {
            description "NAT FROM DMZ to WAN"
            outbound-interface eth0
            source {
                address 172.16.50.0/29
            }
            translation {
                address masquerade
            }
        }
        rule 20 {
            description "NAT FROM MGMT to WAN"
            outbound-interface eth0
            source {
                address 172.16.150.0/24
            }
            translation {
                address masquerade
            }
        }
    }
}
protocols {
    rip {
        interface eth2 {
        }
        network 172.16.50.0/29
    }
    static {
        route 0.0.0.0/0 {
            next-hop 10.0.17.2 {
            }
        }
        route 172.16.200.0/28 {
            next-hop 172.16.150.3 {
            }
        }
    }
}
service {
    dns {
        forwarding {
            allow-from 172.16.50.0/29
            allow-from 172.16.150.0/24
            listen-address 172.16.50.2
            listen-address 172.16.150.2
            system
        }
    }
    ssh {
        listen-address 172.16.150.2
        loglevel verbose
        port 22
    }
}
system {
    config-management {
        commit-revisions 100
    }
    conntrack {
        modules {
            ftp
            h323
            nfs
            pptp
            sip
            sqlnet
            tftp
        }
    }
    console {
        device ttyS0 {
            speed 115200
        }
    }
    host-name fw01-daniel
    login {
        user daniel {
            authentication {
                encrypted-password $6$oI0O1r5PWEXowMSp$9TJUbxNKty4.D1j2rhzb8yfk8eGhSHT3dxOwYNs4P431ISR6o3.ODC8FWIvGMGEEIsblKx6yp9cK4lSVRxbgf.
                public-keys daniel@rw01-daniel {
                    key AAAAB3NzaC1yc2EAAAADAQABAAACAQDZnn/lrKJNT+KPJEfIcRKymL4vICUJScjoz4uVByWRZEJlb+DR+WaRhA/jW3juN9xHnvInR2XvNHUQhV9+GB0D0igbZn57Pi/VQN2lAUkITDLKJl9Aa1sBqzQD592YFamtc/Qyyjh6ec8CqERPQbhRUPt0aTs+J5onM7VTC9qhtJYmD+2/CJRiGAS2D3b8IlwE5UJMuUwTUssFNcjeiCeiGzyIBVBhB8IssuYWsZhrSXb3uVy5AQ4v0ToQzMVQ4gl/0wL2ik2HzdutidVDm0ZOa1JsklJ1Wtm8ufm/WTUTK8Q+GwGus/YLto4vcNAZ7VGo9TYJlUw+wjHa2euyRKhhm3t1MpHhUpE0yiPa525kBMb9AZFxxwfsEkxSgqp/A+irGY9DY/tQvEvy3UT+OlB6Ks0bCx4XGk4X0UIAjdq6i6+sqe6Eyf1Sh5Qm0LOoU1o16vCVGes9k0vA9T8Mv+wkuEy3FEw+u3RzRdckj6BWOXE7z9D7+BhHeJgXVphnpRLiIKuRUcwu1kSTF5vouBCkXS/7rwaP6uWbGhqP2xkW85sL+BZsuTmpK7iKQoEKhlEKCx/mjwpvGEKDQIT1M0ARZLcouDut2zNRSQCa0inYWx7/3+5P6XVTgv+BJbt1If6febI5kOWoN4t70dqDJJ6aLGJUh+GdJbSTgXb9g71Evw==
                    type ssh-rsa
                }
            }
            full-name daniel
        }
        user vyos {
            authentication {
                encrypted-password $6$mf19vObjaWPFXBss$atQjHUYcQ4jcoh0uqJUwX.ODdXdeyhMubm6MSjO9X1TlfNMq5keBseECRfAu.e.9CGaW2p3Pjiblj5ax.wjMG1
                plaintext-password ""
            }
        }
    }
    name-server 10.0.17.2
    ntp {
        server time1.vyos.net {
        }
        server time2.vyos.net {
        }
        server time3.vyos.net {
        }
    }
    syslog {
        global {
            facility all {
                level info
            }
            facility protocols {
                level debug
            }
        }
        host 172.16.50.5 {
            facility authpriv {
                level info
            }
        }
        host 172.16.200.10 {
            facility authpriv {
                level info
            }
            facility kern {
                level debug
            }
            format {
                octet-counted
            }
            port 1514
        }
    }
}
zone-policy {
    zone DMZ {
        from LAN {
            firewall {
                name LAN-to-DMZ
            }
        }
        from WAN {
            firewall {
                name WAN-to-DMZ
            }
        }
        interface eth1
    }
    zone LAN {
        from DMZ {
            firewall {
                name DMZ-to-LAN
            }
        }
        from WAN {
            firewall {
                name WAN-to-LAN
            }
        }
        interface eth2
    }
    zone WAN {
        from DMZ {
            firewall {
                name DMZ-to-WAN
            }
        }
        from LAN {
            firewall {
                name LAN-to-WAN
            }
        }
        interface eth0
    }
}


// Warning: Do not remove the following line.
// vyos-config-version: "bgp@2:broadcast-relay@1:cluster@1:config-management@1:conntrack@3:conntrack-sync@2:dhcp-relay@2:dhcp-server@6:dhcpv6-server@1:dns-forwarding@3:firewall@7:flow-accounting@1:https@3:interfaces@25:ipoe-server@1:ipsec@8:isis@1:l2tp@4:lldp@1:mdns@1:nat@5:nat66@1:ntp@1:openconnect@1:ospf@1:policy@2:pppoe-server@5:pptp@2:qos@1:quagga@9:rpki@1:salt@1:snmp@2:ssh@2:sstp@4:system@22:vrf@3:vrrp@3:vyos-accel-ppp@2:wanloadbalance@3:webproxy@2"
// Release version: 1.4-rolling-202202030910
