//-----------------------------------------------------------------------------
// Copyright (C) 2005-2010 Alfresco Software Limited.
//
// This file is part of Alfresco
//
// Alfresco is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Alfresco is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
//
//
// Author  Jon Cox  <jcox@alfresco.com>
// File    echodns.cpp
//
//-----------------------------------------------------------------------------
//
//
// Overview
// --------
//
//     EchoDNS is a nameserver that creates a wildcard DNS domain
//     for all possible IP addresses;  it does this by requiring
//     that the wildcard corresponds to a domain or subdomain of
//     that IP address encoded with hyphens.
//
//     Note:  According to RFC 2182, you should have at least 2,
//            and preferably 3 nameservers responsible for serving
//            each domain (and no more than 7).  For details, see:
//            http://www.faqs.org/rfcs/rfc2182.html
//
// Example
// -------
//
//     Suppose you make an EchoDNS nameserver listen on:  192.168.1.98
//     Also, suppose the zone's default IP address is:    192.168.1.99
//     and  ns1, ns2, and ns3 are the names of all your
//     EchoDNS servers within the zone being served:      echo.localdomain.lan
//
//     You'd run EchoDNS like this:
//
//            echodns                                             \
//               --user     nobody                                \
//               --serial   2007022601                            \
//               --bind     192.168.1.98                          \
//               --zone_a   echo.localdomain.lan  192.168.1.99    \
//               --ns_a     ns1                     192.168.1.97  \
//               --ns_a     ns2                     192.168.1.98  \
//               --ns_a     ns3                     192.168.1.99  \
//               --log
//
//     Lookup of  moo.cow.10-0-0-5.echo.localdomain.lan  returns  10.0.0.5
//     Lookup of          10-0-0-5.echo.localdomain.lan  returns  10.0.0.5
//     Lookup of          10-0-0-5.echo.localdomain.lan  returns  10.0.0.5
//     Lookup of                   echo.localdomain.lan  returns  192.168.1.99
//     Lookup of               ns1.echo.localdomain.lan  returns  192.168.1.97
//     Lookup of               ns2.echo.localdomain.lan  returns  192.168.1.98
//     Lookup of               ns3.echo.localdomain.lan  returns  192.168.1.99
//
//
//     Note:  The command line above example implies you'd also be runing
//            two other instances of EchoDNS.  This provides redunancy,
//            in case a server (or the machine hosting it) fails.
//            The comand lines for these other instances would be:
//
//            echodns                                             \
//               --user     nobody                                \
//               --serial   2007022601                            \
//               --bind     192.168.1.97                          \
//               --zone_a   echo.localdomain.lan  192.168.1.99    \
//               --ns_a     ns1                     192.168.1.97  \
//               --ns_a     ns2                     192.168.1.98  \
//               --ns_a     ns3                     192.168.1.99  \
//               --log
//
//            echodns                                             \
//               --user     nobody                                \
//               --serial   2007022601                            \
//               --bind     192.168.1.99                          \
//               --zone_a   echo.localdomain.lan  192.168.1.99    \
//               --ns_a     ns1                     192.168.1.97  \
//               --ns_a     ns2                     192.168.1.98  \
//               --ns_a     ns3                     192.168.1.99  \
//               --log
//
//
// General Syntax
// --------------
//
//         echodns                                                  \
//            --user     nobody                                     \
//            --serial   YYYYMMDDXX                                 \
//            --bind     <ip-address-of-echodns-network-interface>  \
//            --zone_a   <zone-name>  <ip-address-of-zone-fqdn>     \
//          { --ns_a     <name-of-server-within-zone>  <ip-of-name> \ }*
//            --ns_a     <name-of-server-within-zone>  <ip-of-name> \
//            --log
//
//
// Motivation
// ----------
//     Alfresco virtualizes content on the basis of name-mangled hostnames
//     (rather than using cookies or request-path mangling).  Thus, it's nice
//     to be able to map wildcarded domains back to a specific IP address.
//
//     The problem is, which IP address?  The answer to that question really
//     depends upon the IP address of the machine you've installed Alfresco's
//     virtualization server on.   Ideally, people in a larger organization
//     would have installed Tinydns, or BIND, or some other nameserver, and
//     they could just configure things for themselves.  However, what about
//     those who just don't want the hassle?
//
//     The common way out of an install problem like this would be to use a
//     nameserver on the Internet that allows wildcarding, and just take
//     advantage of their setup.  Unfortunately (but understandably),
//     companies like  http://dyndns.com require you to register with them,
//     fill out a password, etc.  That's a hassle too... plus hosting
//     something like this carries with it security issues due to the updates.
//
//     Hence the need for EchoDNS.  Because EchoDNS does not take updates,
//     it's much simpler (and theoretically more secure).  Because the
//     name ==> IP address relationship is computed,  there are no big tables
//     to search.  Because the name ==> IP address mapping is fixed, you can
//     max out the TTL to 68 years if you'd like (though some resolvers would
//     not respect a value like this).  That said, there are actually reasons
//     to keep TTLs down to 1 day:
//
//       o  Logging can give *some* idea of relative activity
//       o  New nameservers can't stay "poisoned" for long
//       o  Mistakes can always be undone in a day
//
//
// Details
// -------
//     EchoDNS infers the IP address to return from the host label prior
//     to the zone it serves (and any subdomains of that host label).
//
//     This IP-bearing host label is expected to contain digits separated
//     by hyphens (in reality, any non-digit will do, but hyphens are
//     used in all examples by convention).
//
//     For example, suppose EchoDNS is authoritative for
//     the zone  "echo.localdomain.lan", and it is asked
//     to translate the following FQDN into an IP address:
//
//          moo.cow.192-168-1-5.echo.localdomain.lan
//
//     Its response would be:   192.168.1.5
//
//     For most illegal/unknown hypen-encoded IP address,
//     EchoDNS returns the loopback IP address:  127.0.0.1
//
//     There are two exceptions to this rule:
//
//     [1]  When asked about the IP address of the domain in which an EchoDNS
//          nameserver itself resides, EchoDNS returns the value associated
//          with the --zone_a command-line argument it was given.  For example,
//          suppose  --zone_a were specified on the command line as:
//
//              --zone_a echo.localdomain.lan  192.168.1.99
//
//          When asked for the address of echo.localdomain.lan,
//          EchoDNS will respond:  192.168.1.99
//
//
//     [2]  When asked about the IP address of a nameserver
//          within --zone_a, the value returned is the IP
//          address of the associated --ns_a argument.
//          For example, suppose the command line included:
//
//              --ns_a  ns1   192.168.1.97 \
//              --ns_a  ns2   192.168.1.98 \
//              --ns_a  ns3   192.168.1.99
//
//          When asked for the address of ns2.echo.localdomain.lan
//          EchoDNS will respond:  192.168.1.98
//
//
//     The reasons for exception [2] are somewhat subtle.
//     Here's what's going on:
//
//     If EchoDNS was delegated authority within some zone
//     (e.g.:  echo.localdomain.net), and the glue record
//     within the delegating nameserver said that the EchoDNS
//     machine was named  "ns2", and it provided some IP
//     address for it (eg: 192.168.1.98), then EchoDNS *must*
//     respond to direct queries for this name with the same
//     IP address that the delegating nameserver  provided.
//     This is necessary to avoid poisoning the client's
//     resolver cache, becuase EchoDNS is authoritative
//     within its zone (the delegating nameserver is not).
//
//     For example, suppose a BIND-based nameserver delegated
//     "echo" to a set of name servers running EchoDNS like this:
//
//      ;-------------------------------------------------------------
//      ; Delegation of echo.localdomain.lan zone to EchoDNS servers
//      ;-------------------------------------------------------------
//      echo.localdomain.lan.     IN  NS ns1.echo.localdomain.lan.
//      echo.localdomain.lan.     IN  NS ns2.echo.localdomain.lan.
//      echo.localdomain.lan.     IN  NS ns3.echo.localdomain.lan.
//
//      ;-------------------------------------------------------------
//      ; Glue records for the nameservers in echo.localdomain.lan
//      ;-------------------------------------------------------------
//      ns1.echo.localdomain.lan. IN  A  192.168.1.97
//      ns2.echo.localdomain.lan. IN  A  192.168.1.98
//      ns3.echo.localdomain.lan. IN  A  192.168.1.99
//
//
//     Equivalently, suppose the delegation in tinydns looked like this:
//
//      # Delegate echo.localdomain.lan to nsX.echo.localdomain.lan
//      &echo.localdomain.lan:192.168.1.97:ns1.echo.localdomain.lan
//      &echo.localdomain.lan:192.168.1.98:ns1.echo.localdomain.lan
//      &echo.localdomain.lan:192.168.1.98:ns1.echo.localdomain.lan
//
//              Aside:  Friends don't let friends use BIND.
//
//     Now suppose the answer you wanted all the EchoDNS nameservers to
//     hand back for echo.localdomain.lan itself was:    192.168.1.99
//
//     The command to run EchoDNS are:
//
//     On 192.168.1.97, run:
//         echodns \
//                  --user       nobody                                \
//                  --serial     2007022601                            \
//                  --bind       192.168.1.97                          \
//                  --zone_a     echo.localdomain.lan    192.168.1.99  \
//                  --ns_a       ns1                     192.168.1.97  \
//                  --ns_a       ns2                     192.168.1.98  \
//                  --ns_a       ns3                     192.168.1.99
//
//     On 192.168.1.98, run:
//         echodns \
//                  --user       nobody                                \
//                  --serial     2007022601                            \
//                  --bind       192.168.1.98                          \
//                  --zone_a     echo.localdomain.lan    192.168.1.99  \
//                  --ns_a       ns1                     192.168.1.97  \
//                  --ns_a       ns2                     192.168.1.98  \
//                  --ns_a       ns3                     192.168.1.99
//
//     On 192.168.1.99, run:
//         echodns \
//                  --user       nobody                                \
//                  --serial     2007022601                            \
//                  --bind       192.168.1.99                          \
//                  --zone_a     echo.localdomain.lan    192.168.1.99  \
//                  --ns_a       ns1                     192.168.1.97  \
//                  --ns_a       ns2                     192.168.1.98  \
//                  --ns_a       ns3                     192.168.1.99
//
//
//
// Limitations
// -----------
//     o  Only queries for A, NS, and SOA records are supported.
//        There's no provision at all for query types such as MX, etc.,
//        other than retuning 0 answers.  EchoDNS is not a general-purpose
//        name server.  It is intended for providing wildcard DNS records
//        for hyphen-encoded domains, and it's assumed that you're delegating
//        some subdomain to it from a general-purpose DNS server
//        (e.g.: tinydns, BIND, etc.).
//
//     o  No support for delegation of sub-zones within an EchoDNS zone.
//        This constraint follows from the fact that EchoDNS defines
//        an IP address for all names within its SOA.
//
//     o  No support for IPv6 (no AAAA or A6 records).
//        One day, EchoDNS will support AAAA records, but there is no
//        plan to *ever* support A6.  A6 is dangerous and pointless.
//
//     o  No support for EDNS0  ("Extended DNS 0" - RFC 2671).
//        Therefore responses over 512 bytes are truncated (as is normal
//        for "non-extended" DNS over UDP).  Some clients advertised the
//        ability to handle rather large UDP packets (e.g.: 4096 bytes),
//        but EchoDNS will ignore such advertisements.
//
//     o  UDP only  (no TCP-based interface).
//        This is unimportant, because zone transfers (the application of TCP)
//        make no sense for EchoDNS. Do you really want to transfer every a
//        mapping for every possible IP address?  I don't think so!
//        UDP is the default binding for everything else.
//
//     o  EchoDNS can't tell who's *really* asking it for data.
//        This limitation is shared by all DNS servers.  Here's why:
//        applications (e.g.: browsers) typically have stub resolvers;
//        it's the client machine's nameserver (e.g.: dnscache, tinydns, BIND)
//        that does the actual name lookup.   Most home users configure their
//        system to use the nameserver offered by their ISP, and IPSs may
//        or may not respect the TTL off the answers that EchoDNS provides.
//        Thus, many distinct users may be represented behind a single IP
//        address seen by EchoDNS.  Graphically it looks like this:
//
//                     +------------------------------+
//                     |  Client host                 |  The "real" user's
//                     |  non-recursive resolver stub |  request is proxied
//                     +-----------------^------------+
//                                |      |
//                     +----------v-------------------+
//                     |  Client's nameserver         |  IP address seen
//                     |  provides recursive          |  by "external"
//                     |  lookup on behalf of client  |  nameservers
//                     |  (and probably a big cache)  |
//                     +------^----------^--------^---+
//                         |  |       |  |     |  |
//        ................................................................
//                         |  |       |  |     |  |
//                         |  |       |  |     |  |      EchoDNS cannot
//                         |  |       |  |     |  |      know the "real"
//                         |  |       |  |     |  |      client IP address
//                         |  |       |  |     |  |
//                         |  |       |  |     |  |
//              ,----------'  |       |  |     |  `------------.
//              |  ,----------'       |  |     `------------.  |
//              |  |                  |  |                  |  |
//        +-----V-----------+  +------v----------+  +-------v------------+
//        | root nameserver |  | .com nameserver |  | xxx.com nameserver |
//        +-----------------+  +-----------------+  +--------------------+
//
//
//
// See also: http://www.zytrax.com/books/dns/ch2/index.html#recursive
// To validate your setup, see: http://www.dnsreport.com
//
// To build this program, type:
//      g++ -o echodns  echodns.cpp
//
//-----------------------------------------------------------------------------
/*

  Understanding how this program works requires you to look
  at the RFC regarding the DNS UDP wire protocol (RFC 1035).
  While DNS servers usually offer both UDP and TCP, EchoDNS
  only offers UDP.  The reason


    DNS  UDP packets have the following 5 sections:

        UDP itself hsa an 8 byte header:  srcport=2
                                          dstport=2
                                          length=2
                                          checksum=2

     The DNS UDP wire protocol limits the length of
     its UDP data packet to 512 bytes.

    This data packet includes the following 5 sections:

     Header         ID & counts for each following section
     Question       the question for the name server
     Answer RRs     answering the question             (e.g.: "A" records)
     Authority      RRs pointing toward an authority   (e.g.: ns delegations)
     Additional     RRs holding additional information (e.g.: glue records)


  Header
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  http://www.freesoft.org/CIE/RFC/1035/40.htm

   The 12-byte DNS header has the same format for requests and responses:

   +---+---+---+---+---+---+---+---+ +---+---+----+----+----+----+----+----+
   | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | | 8 | 9 | 10 | 11 | 12 | 13 | 14 | 15 |
   +---+---+---+---+---+---+---+---+ +---+---+----+----+----+----+----+----+
   |                               ID                                      |
   +---+---+---+---+---+---+---+---+ +---+---+----+----+----+----+----+----+
   | QR|     Opcode    | AA| TC| RD| | RA|      Z      |      RCODE        |
   +---+---+---+---+---+---+---+---+ +---+---+----+----+----+----+----+----+
   |                             QDCOUNT     (entries in question)         |
   +---+---+---+---+---+---+---+---+ +---+---+----+----+----+----+----+----+
   |                             ANCOUNT     (entries in answer)           |
   +---+---+---+---+---+---+---+---+ +---+---+----+----+----+----+----+----+
   |                             NSCOUNT     (nameservers in authority)    |
   +---+---+---+---+---+---+---+---+ +---+---+----+----+----+----+----+----+
   |                             ARCOUNT     (entries in additional)       |
   +---+---+---+---+---+---+---+---+ +---+---+----+----+----+----+----+----+

   ID  - A 16 bit identifier assigned by the program that generates any kind
         of query. This identifier is copied the corresponding reply and can
         be used by the requester to match up replies to outstanding queries.

   QR  -  one bit field that specifies whether this message is
          a query (0), or a response (1).

   OPCODE -
         A four bit field that specifies kind of query in this message.
         This value is set by the originator of a query and copied into
         the response. The values are:

               0  a standard query (QUERY)
               1  an inverse query (IQUERY)
               2  server status request (STATUS)
            3-15  reserved for future use

   AA  -  Authoritative Answer.
          This bit is valid in responses, and specifies that the
          responding name server is an authority for the domain name
          in question section.

   TC     TrunCation - specifies that this message was truncated due to length
          greater than that permitted on the transmission channel.

   RD     Recursion Desired - this bit may be set in a query and is copied into
          the response. If RD is set, it directs the name server to pursue the
          query recursively. Recursive query support is optional.

   RA     Recursion Available - this be is set or cleared in a response,
          and denotes whether recursive query support is available in the
          name server.

   Z      Reserved for future use. Must be zero in all queries and responses.

   RCODE   Response code - this 4 bit field is set as part of responses.
           The values have the following interpretation:

           Value   RCODE Name    Description

           0       NOERROR       No error condition

           1       FORMERR       Format error
                                 The name server was unable to interpret
                                 the query.

           2       SERVFAIL      Server failure
                                 The name server was unable to process this
                                 query due to a problem with the name server.

           3       NXDOMAIN      Name Error
                                 Meaningful only for responses from an
                                 authoritative name server, this code signifies
                                 that the domain name referenced in the query
                                 does not exist.

           4       NOTIMP        Not Implemented
                                 The name server does not support the requested
                                 kind of query.

           5       REFUSED       Refused
                                 The name server refuses to perform the
                                 specified operation for policy reasons.
                                 For example, a name server may not wish to
                                 provide the information to the particular
                                 requester, or a name server may not wish to
                                 perform a particular operation (e.g.: zone
                                 transfer) for particular data.

           6-15                  Reserved for future use.


   QDCOUNT   An unsigned 16 bit integer specifying the number of entries
             in the question section.

                Implementation note:
                    BIND does not support QDCOUNT values other than
                    0 or 1; resolvers must accept BIND as the lowest
                    common-denominator, due to its near ubiquity.
                    Thus, DNS RFC 1035 moot on this issue; it does not
                    make much sense to support QDCOUNT values higher
                    than 1 in EchoDNS... so it won't!


   ANCOUNT   An unsigned 16 bit integer specifying the number of resource
             records in the answer section.

   NSCOUNT   An unsigned 16 bit integer specifying the number of name server
             resource records in the authority records section.

   ARCOUNT   an unsigned 16 bit integer specifying the number of resource
             records in the additional records section.



  Question        the question for the name server
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  http://www.freesoft.org/CIE/RFC/1035/41.htm

   +---+---+---+---+---+---+---+---+ +---+---+----+----+----+----+----+----+
   | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | | 8 | 9 | 10 | 11 | 12 | 13 | 14 | 15 |
   +---+---+---+---+---+---+---+---+ +---+---+----+----+----+----+----+----+
   /                                                                       /
   /                              QNAME    (variable # bytes)              /
   /                                                                       /
   +---+---+---+---+---+---+---+---+ +---+---+----+----+----+----+----+----+
   |                              QTYPE    (2 bytes)                       |
   +---+---+---+---+---+---+---+---+ +---+---+----+----+----+----+----+----+
   |                              QCLASS   (2 bytes)                       |
   +---+---+---+---+---+---+---+---+ +---+---+----+----+----+----+----+----+


    QNAME  -   Domain names in messages are expressed in terms of a sequence of
               labels. Each label is represented as a one octet length field
               followed by that number of octets. Since every domain name ends
               with the null label of the root, a domain name is terminated by
               a length byte of zero. The high order two bits of every length
               octet must be zero, and the remaining six bits of the length
               field limit the label to 63 octets or less.

               To simplify implementations, the total length of a domain name
               (i.e., label octets and label length octets) is restricted to
               255 octets or less.

               Although labels can contain any 8 bit values in octets that make
               up a label, it is strongly recommended that labels follow the
               preferred syntax described elsewhere in this memo, which is
               compatible with existing host naming conventions. Name servers
               and resolvers must compare labels in a case-insensitive manner
               (i.e., A=a), assuming ASCII with zero parity.  Non-alphabetic
               codes must match exactly.


    QTYPE  -   A two octet code which specifies the type of the query.
               The values for this field include all codes valid for
               a TYPE field, together with some more general codes which
               can match more than one TYPE of RR.

                 Value   QTYPE    Meaning
                 1       A        a host address
                 2       NS       an authoritative name server
                 3       MD       a mail destination (Obsolete - use MX)
                 4       MF       a mail forwarder (Obsolete - use MX)
                 5       CNAME    the canonical name for an alias
                 6       SOA      marks the start of a zone of authority
                 7       MB       a mailbox domain name (EXPERIMENTAL)
                 8       MG       a mail group member (EXPERIMENTAL)
                 9       MR       a mail rename domain name (EXPERIMENTAL)
                 10      NULL     a null RR (EXPERIMENTAL)
                 11      WKS      a well known service description
                 12      PTR      a domain name pointer
                 13      HINFO    host information
                 14      MINFO    mailbox or mail list information
                 15      MX       mail exchange
                 16      TXT      text strings
                 28      AAAA     ipv6 host address (RFC 1886)
                 38      A6       ipv6 host address (RFC 2874).  A6 == harmful
                 252     AXFR     request for entire zone
                 253     MAILB    request for mailbox-related RR (MB,MG, or MR)
                 254     MAILA    request for mail agent RRs (obsolete, see MX)
                 255     *        request for all records

    QCLASS  -  A two octet code that specifies the class of the query.
               For example, the QCLASS field is IN for the Internet.
               QCLASS is a superset of CLASS (adds "any class"):

                 Value    QCLASS   Meaning
                 1        IN       the Internet
                 2        CS       the CSNET class (Obsolete)
                 3        CH       the CHAOS class
                 4        HS       Hesiod [Dyer 87]
                 5        *        any class




   Answer     RRs answering the question
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   http://www.freesoft.org/CIE/RFC/1035/42.htm

   +---+---+---+---+---+---+---+---+ +---+---+----+----+----+----+----+----+
   | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | | 8 | 9 | 10 | 11 | 12 | 13 | 14 | 15 |
   +---+---+---+---+---+---+---+---+ +---+---+----+----+----+----+----+----+
   /                                                                       /
   /                              NAME     (variable # bytes)              /
   /                                                                       /
   +---+---+---+---+---+---+---+---+ +---+---+----+----+----+----+----+----+
   |                              TYPE     (2 bytes)                       |
   +---+---+---+---+---+---+---+---+ +---+---+----+----+----+----+----+----+
   |                              CLASS    (2 bytes)                       |
   +---+---+---+---+---+---+---+---+ +---+---+----+----+----+----+----+----+
   |                                                                       |
   |............................. TTL .....(4 bytes).......................|
   |                                                                       |
   +---+---+---+---+---+---+---+---+ +---+---+----+----+----+----+----+----+
   |                              RDLENGTH (2 bytes)                       |
   +---+---+---+---+---+---+---+---+ +---+---+----+----+----+----+----+----+
   /                                                                       /
   /                              RDATA    (variable # bytes)              /
   /                                                                       /
   +---+---+---+---+---+---+---+---+ +---+---+----+----+----+----+----+----+


   NAME  -     A domain-name to which this resource record pertains.

   TYPE  -     two octets containing one of the RR type codes.
               This field specifies the meaning of the data in the RDATA field.
               TYPE fields are used in resource records.
               Note that these types are a subset of QTYPEs.

                 Value    TYPE     Meaning
                 1        A        a host address
                 2        NS       an authoritative name server
                 3        MD       a mail destination (Obsolete - use MX)
                 4        MF       a mail forwarder (Obsolete - use MX)
                 5        CNAME    the canonical name for an alias
                 6        SOA      marks the start of a zone of authority
                 7        MB       a mailbox domain name (EXPERIMENTAL)
                 8        MG       a mail group member (EXPERIMENTAL)
                 9        MR       a mail rename domain name (EXPERIMENTAL)
                 10       NULL     a null RR (EXPERIMENTAL)
                 11       WKS      a well known service description
                 12       PTR      a domain name pointer
                 13       HINFO    host information
                 14       MINFO    mailbox or mail list information
                 15       MX       mail exchange
                 16       TXT      text strings
                 28       AAAA     ipv6 host address (RFC 1886).  Implement!
                 38       A6       ipv6 host address (RFC 2874).  A6 == harmful



   CLASS -     Two octets that specify the class of the data in the RDATA field

   TTL   -     A 32 bit unsigned integer that specifies the time interval
               (in seconds) that the resource record may be cached before
               it should be discarded. Zero values are interpreted to mean
               that the RR can only be used for the transaction in progress,
               and should not be cached.

   RDLENGTH -  An unsigned 16 bit integer that specifies the length in octets
               of the RDATA field.

   RDATA    -  A variable length string of octets that describes the resource.
               The format of this information varies according to the TYPE
               and CLASS of the resource record. For example, the if the TYPE
               is A and the CLASS is IN, the RDATA field is a 4 octet ARPA
               Internet address.  If the TYPE is NS and the CLASS is IN, the
               RDATA field is formated like a NAME (QNAME), and can make use
               of compression via pointers (see below).



  Authority       RRs pointing toward an authority
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  Same structure as "answer"

  Additional      RRs holding additional information
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  Same structure as "answer"



  Compression:
        In order to reduce the size of messages, the domain system utilizes a
        compression scheme which eliminates the repetition of domain names in
        a message. In this scheme, an entire domain name or a list of labels
        at the end of a domain name is replaced with a pointer to a prior
        occurrence of the same name.

        The pointer takes the form of a two octet sequence:

   +---+---+---+---+---+---+---+---+ +---+---+----+----+----+----+----+----+
   | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | | 8 | 9 | 10 | 11 | 12 | 13 | 14 | 15 |
   +---+---+---+---+---+---+---+---+ +---+---+----+----+----+----+----+----+
   | 1 | 1 |                      OFFSET                                   |
   +---+---+---+---+---+---+---+---+ +---+---+----+----+----+----+----+----+

   The first two bits are ones. This allows a pointer to be distinguished from
   a label, since the label must begin with two zero bits because labels are
   restricted to 63 octets or less. (The 10 and 01 combinations are reserved
   for future use.) The OFFSET field specifies an offset from the start of the
   message (i.e., the first octet of the ID field in the domain header). A
   zero offset specifies the first byte of the ID field, etc.

   The compression scheme allows a domain name in a message to
   be represented as either:

        * a sequence of labels ending in a zero octet
        * a pointer
        * a sequence of labels ending with a pointer

   Messages carried by UDP are restricted to 512 bytes
   (not counting the IP or UDP headers).  Longer messages
   are truncated and the TC bit is set in the header.

*/
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------


#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <sys/uio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <ctype.h>
#include <pwd.h>
#include <sys/types.h>

static const char *Version = "1.5";



// Normal A records have a "time to live" of 1 day, by default.
#define DEFAULT_A_RR_TTL        "86400"


// NS records & their A record "glue have a TTL of 2 days, by default.
#define DEFAULT_NS_A_RR_TTL     "172800"




//-----------------------------------------------------------------------------
// EchoDNS_server --
//      Implemenation of EchoDNS server.
//-----------------------------------------------------------------------------
class EchoDNS_server
{
  public:
    enum SOAparam
    {
        SOA_MINIMUM_TTL = 2560,
        SOA_REFRESH     = 16384,
        SOA_RETRY       = 2048,
        SOA_EXPIRE      = 1048576
    };

    enum Rcode          // Server response codes
    {
        RCODE_NOERROR  = 0,
        RCODE_FORMERR  = 1,
        RCODE_SERVFAIL = 2,
        RCODE_NXDOMAIN = 3,
        RCODE_NOTIMP   = 4,
        RCODE_REFUSED  = 5
    };

    enum RRtype
    {
        RRTYPE_A       = 1,    // a host address
        RRTYPE_NS      = 2,    // an authoritative name server
        RRTYPE_MD      = 3,    // a mail destination (Obsolete - use MX)
        RRTYPE_MF      = 4,    // a mail forwarder (Obsolete - use MX)
        RRTYPE_CNAME   = 5,    // the canonical name for an alias
        RRTYPE_SOA     = 6,    // marks the start of a zone of authority
        RRTYPE_MB      = 7,    // a mailbox domain name (EXPERIMENTAL)
        RRTYPE_MG      = 8,    // a mail group member (EXPERIMENTAL)
        RRTYPE_MR      = 9,    // a mail rename domain name (EXPERIMENTAL)
        RRTYPE_NULL    = 10,   // a null RR (EXPERIMENTAL)
        RRTYPE_WKS     = 11,   // a well known service description
        RRTYPE_PTR     = 12,   // a domain name pointer
        RRTYPE_HINFO   = 13,   // host information
        RRTYPE_MINFO   = 14,   // mailbox or mail list information
        RRTYPE_MX      = 15,   // mail exchange
        RRTYPE_TXT     = 16,   // text strings
        RRTYPE_AAAA    = 28,   // ipv6 host address (RFC 1886)
        RRTYPE_A6      = 38,   // ipv6 host address (RFC 2874).  A6 == harmful
        RRTYPE_AXFR    = 252,  // request for entire zone
        RRTYPE_MAILB   = 253,  // request for mailbox-related RR (MB,MG, or MR)
        RRTYPE_MAILA   = 254   // request for mail agent RRs (obsolete, see MX)
    };

    enum MiscConstants
    {
        EFFECTIVE_USER_BUF_SIZE = 256,

        // EchoDNS does not support for EDNS0 (RFC 2671),
        // so UDP-based messages can't be absurdly large.
        //
        MAX_MESG_SIZE           = 512,

        // Buffer large enough to hold any legal host label + NULL
        HOST_LABEL_BUF_SIZE     = 64,

        // Buffer size large enough to hold any dotted quad + NULL
        // Example:  aaa.bbb.ccc.ddd<null>
        //
        DOTTED_QUAD_BUF_SIZE    = 16,

        // Maximum number of nameservers for this zone.  Zones should have at least
        // two nameservers, a single failure is not catastrophic.   In practice, 3
        // should be plenty, but let's use an absurdly high number to avoid all
        // real-world limitations.
        //
        MAX_NS_ZONE_COUNT       = 32,


        // According to RFCs: 608, 810, 952, 1035, and 1123,
        // no host label can be more than 63 bytes,
        // and the total of all labels + dots must
        // be no more than 255 bytes.
        //
        // Thus, largest buffer needed to hold a dns name in DNS UDP
        // "name/qname" format is 257 bytes:
        //
        //         1 leading byte for length of 1st segment
        //      +  255 "body" of labels and dots
        //      +  1 terminating null byte for last label
        //      ------------------------------------------
        //        257
        //
        ZONE_NAME_BUFFER_SIZE = 257
    };



    EchoDNS_server(const char *program_name, FILE *output_file);
    void  bind_UDP_socket();
    void  process_requests();
    void  qtype_A_handler();
    void  qtype_NS_handler();
    void  qtype_SOA_handler();
    void  qtype_GENERIC_handler(unsigned short, Rcode status);

    void  set_ttl_a(int ttl);
    void  set_ttl_ns_a(int ttl);

    void  set_effective_user(const char *p);
    void  set_ipv4_bind_address( const char *dotted_quad );
    void  set_port(int port);
    void  set_zone_a(const char *z_name,   const char *z_quad);
    void  add_ns_a(  const char *ns_label, const char *ns_quad);
    int   get_ns_host_count();
    void  set_ipaddr_array(int ip_quad[4], const char *dotted_quad);
    void  emit_udp_packet(int size);
    void  set_soa_serial(unsigned int soa_serial);

    const char *get_zone_fqdn();
    const char *get_bind_quad();
    void        set_log_requests(bool tf) { log_requests_ = tf;}
    void        set_debug_server(bool tf) { debug_server_ = tf;}
    bool        get_log_requests()        { return log_requests_;}
    bool        query_equals_zone_name();


  private:
    bool log_requests_;
    bool debug_server_;
    int  udp_sock_;

    const char  *prog_name_;                // ususally set to argv[0]
    FILE        *ofile_;                    // usually stderr

    char  effective_user_[ EFFECTIVE_USER_BUF_SIZE ];   // Flawfinder: ignore
    unsigned char mesg_[MAX_MESG_SIZE];                 // Flawfinder: ignore
    unsigned char resp_[MAX_MESG_SIZE];                 // Flawfinder: ignore

    // offsets into UDP packet of QNAME labels
    int  qname_label_offset_[256];                      // Flawfinder: ignore

    int  eoq_;                                          // end of QNAME offset

    // precomputed name lengths
    int  ns_host_name_length_[MAX_NS_ZONE_COUNT];       // Flawfinder: ignore

    // nameservers within zone
    int  ns_host_count_;

    // Examples: ns1, ns2,...
    char ns_host_name_[MAX_NS_ZONE_COUNT]               // Flawfinder: ignore
                      [HOST_LABEL_BUF_SIZE];            // Flawfinder: ignore

    // ns_a addr e.g.: {10,0,0,1}
    int  ns_host_addr_[MAX_NS_ZONE_COUNT][4];           // Flawfinder: ignore

    // IP address of zone fqdn
    int  zone_addr_[4];                                 // Flawfinder: ignore

    // DNS UDP name format
    unsigned char zone_name_[ ZONE_NAME_BUFFER_SIZE ];  // Flawfinder: ignore

    int zone_labels_;     // example.com. == 3  because the trailing
                          // dot is treated as the the empty label ("").
                          //
                          //        "example"  == 1
                          //        "com"      == 2
                          //        ""         == 3

    unsigned int  ttl_a_in_network_order_;        // 1 day
    unsigned int  ttl_ns_a_in_network_order_;     // 2 days

    unsigned int  soa_ttl_minimum_in_network_order_;
    unsigned int  soa_ttl_refresh_in_network_order_;
    unsigned int  soa_ttl_retry_in_network_order_;
    unsigned int  soa_ttl_expire_in_network_order_;
    unsigned int  soa_serial_in_network_order_;

    // fqdn of this zone
    char        zone_fqdn_[256];                        // Flawfinder: ignore
    int         zone_length_;
    int         server_port_;
    int         qname_label_count_;                     // labels in question
    sockaddr_in server_addr_;
    sockaddr_in client_addr_;
    socklen_t   sizeof_client_addr_;

    // IP this server listens on
    char bind_quad_[ DOTTED_QUAD_BUF_SIZE ];            // Flawfinder: ignore

    // An invariant block of memory used for making it easy
    // to avoid handing out the network address "0.0.0.0"
    // as an answer to a query.

    int zero_quad[4];                                   // Flawfinder: ignore
};

//-----------------------------------------------------------------------------
// EchoDNS_server --
//      Constructor for EchoDNS server
//-----------------------------------------------------------------------------
EchoDNS_server::EchoDNS_server(const char *program_name, FILE *output_file)
{
    log_requests_      = false;
    debug_server_      = false;
    prog_name_         = program_name;
    ofile_             = output_file;            // typically, stderr

    server_port_       = 53;
    zone_labels_       = 1;

    ns_host_count_     = 0;
    udp_sock_          = 0;
    zone_fqdn_[0]      = 0;
    effective_user_[0] = 0;
    bind_quad_[0]      = 0;
    qname_label_count_ = 0;


    // Flawfinder: ignore
    ttl_a_in_network_order_       = htonl( atoi( DEFAULT_A_RR_TTL   ) ) ;

    // Flawfinder: ignore
    ttl_ns_a_in_network_order_    = htonl( atoi( DEFAULT_NS_A_RR_TTL) );

    soa_ttl_minimum_in_network_order_ = htonl( SOA_MINIMUM_TTL );
    soa_ttl_refresh_in_network_order_ = htonl( SOA_REFRESH     );
    soa_ttl_retry_in_network_order_   = htonl( SOA_RETRY       );
    soa_ttl_expire_in_network_order_  = htonl( SOA_EXPIRE      );

    // Cute trick:
    //    Suppose EchoDNS is executed from a DJBDNS-style "run" script
    //    You can pass the serial number like this:
    //
    //             --serial  `/usr/bin/stat --format="%Y" $0`
    //
    //    This way, any time you modify the 'run' script, you automaticlly
    //    get a new serial number.   To make this work, you should use
    //    "rsync -a" to copy new "run" files from machine to machine
    //    (thereby preserving mtime values).
    //
    soa_serial_in_network_order_  = 1;    // see also:  {-s|--serial} flag

    memset(ns_host_name_, 0, sizeof(ns_host_name_));
    memset(ns_host_addr_, 0, sizeof(ns_host_addr_));
    memset(zero_quad,    0, sizeof(zero_quad) );
    memset(ns_host_name_length_, 0, sizeof(ns_host_name_length_));
}


//-----------------------------------------------------------------------------
// bind_UDP_socket --
//      Bind a UDP socket, then seteuid/setegid to effective_user_,
//      if possible.
//
//      Note:  The change of eid/gid happens after the bind because typically
//             EchoDNS must bind to port 53, which requires root privileges.
//-----------------------------------------------------------------------------
void EchoDNS_server::bind_UDP_socket()
{
    sizeof_client_addr_ = sizeof(client_addr_);
    memset(&server_addr_, 0, sizeof(server_addr_));

    // Create UDP socket

    if ( (udp_sock_ = socket( AF_INET, SOCK_DGRAM, IPPROTO_UDP ) ) == -1 )
    {
        perror("socket()");
        exit(1);
    }

    server_addr_.sin_family      = AF_INET;                  // Internet addr
    server_addr_.sin_port        = htons(server_port_);       // server port
    server_addr_.sin_addr.s_addr = inet_addr( bind_quad_ );   // bind IP addr


    if ( -1 ==
         bind( udp_sock_,(struct sockaddr *)&server_addr_,sizeof(server_addr_))
       )
    {
        perror("bind()");

        fprintf(ofile_, "\n\n"
          "ERROR: %s:\n"
          "  Mmake sure you don't have another nameserver that's\n"
          "  just grabbing port 53 on all interfaces.\n\n"
          "  To force BIND (aka: 'named') to be more selective about\n"
          "  the ports it uses, configure it with an explicit list\n"
          "  of IP addresses using the 'listen-on' option.  For example:\n"
          "  \n"
          "          listen-on port 53 { 127.0.0.1; 192.168.1.5; };\n\n"
          "  A BIND configuration like this only listens on port 53\n"
          "  of the loopback interface (127.0.0.1), and 192.168.1.5.\n"
          "  If you have an configured an additional interface using\n"
          "  a command on UNIX such as:\n\n"
          "     ifconfig eth0:1 192.168.1.99 netmask 255.255.255.0\n\n"
          "  then  %s can use port 53 on  192.168.1.99\n"
          "  (thus %s and BIND won't compete).\n\n",
          prog_name_, prog_name_, prog_name_
        );

        exit(1);
    }

    if ( effective_user_ )
    {
        struct passwd *pw = getpwnam( effective_user_ );
        if ( pw )
        {
            seteuid( pw->pw_uid );
            setegid( pw->pw_gid );
        }
        else
        {
            fprintf(ofile_,"ERROR: %s:  attempt to set euid/egid "
                            "to user that does not exist: %s\n",
                             prog_name_, effective_user_);
            exit(1);
        }
    }
}




//-----------------------------------------------------------------------------
// process_requests --
//      Read and respond to requests in an infinite loop.
//      The "header" and "question" sections are filled in,
//      leaving other handlers to deal with the "answer",
//      "authority", and "additional" sections.
//
//      Note:  Other handlers may discover that the generic values used in
//             the 'answer' section need to be altered.  Fixing the generic
//             values set here is their responsibility (e.g.: the RCODE,
//             ANCOUNT, RCOUNT).
//
//-----------------------------------------------------------------------------
void EchoDNS_server::process_requests()
{
    // Sanity check the config before proceeding:
    //
    //          Because the UDP packet must be < 512 bytes, the domain
    //          EchoDNS is authoritative for cannot include a huge number
    //          of listed nameservers with long names.  Otherwise, an
    //          'NS' lookup won't be able to return a valid set of answers
    //          plus their associated glue records.   The formula is:
    //
    //                             ns_host_count_
    //                                ----
    //                                \
    //           42 + zone_length_ +  /    24  + 2 * ns_host_name_length_[i]
    //                                ----
    //                                i = 0

    int ns_lookup_response_length = 42 + zone_length_;

    for (int i=0; i< ns_host_count_ ; i++)
    {
        ns_lookup_response_length +=  24 +  2 * ns_host_name_length_[i];
    }

    if ( ns_lookup_response_length >= MAX_MESG_SIZE )
    {
        fprintf(ofile_,
                "ERROR: %s  Names given by --ns_a flag would be truncated "
                            "within NS lookups (too much data)\n", prog_name_);
        exit(1);
    }


    for (;;)
    {
        qname_label_count_ = 0;    // labels in question

        memset(&client_addr_, 0, sizeof(client_addr_));

        if (  -1 ==  recvfrom( udp_sock_,
                               mesg_,
                               MAX_MESG_SIZE,
                               0,
                               (struct sockaddr *) &client_addr_,
                               &sizeof_client_addr_
                             )
           )
        {
            perror("recvfrom()");
            exit(1);
        }

        //-----------------------------------
        // Header section
        //-----------------------------------

        // copy ID of mesg_ to resp_
        resp_[0] = mesg_[0];
        resp_[1] = mesg_[1];

        // Only non-truncated authoritative resource records are ever served.
        //
        //                    QR=1=response   AA=1=authoritative  TC=0=non-truc
        //                      1000 0000      00000100           FFFF 1101
        resp_[2] = ( mesg_[2]  |  0x80       |  0x04 )             & 0xFD;


        // EchoDNS is a non-caching, content-only DNS server, not a resolver.
        // It won't do recursion any client (not even local ones).
        //
        //         RA    = 0  (recursion not available)
        //         Z     = 0  (per RFC 1035)
        //         RCODE = 0  (successful)
        //
        resp_[3] = RCODE_NOERROR;


        // QDCOUNT
        //      There's no reason to deal with more than one question at a time
        //      in a packet because this is an easy way to make the EchoDNS
        //      server do much more work than its (potential) attacker.
        //      BIND does not support this feature either, probably for
        //      the same reason.  Because BIND does not support it, no sane
        //      resolver would ask multiple questions in the same packet anyhow,
        //      thereby mooting the RFC.   For that reason, EchoDNS will just
        //      answer the first question, whatever that happens to be.
        //
        resp_[4] =  0;       //  Don't trust:    mesg_[4]
        resp_[5] =  1;       //  Don't trust:    mesg_[5]


        // ANCOUNT
        //      This is set to 0 later there's an error,
        //      or to  ns_host_count_in_network_order
        //      for NS records (there may be more than 1).
        //
        resp_[6] =  0;
        resp_[7] =  1;


        // NSCOUNT
        //      There are never any authority records because EchoDNS
        //      never delegates subdomains to any other nameserver.
        //
        resp_[8] =  0;
        resp_[9] =  0;


        // ARCOUNT
        //      Additional records.
        //      By default, there are no additional records, but
        //      this is reset to ns_host_count_in_network_order
        //      for NS records (A-record glue is supplied for each).
        //
        //      No support for EDNS0 (RFC 2671).
        //
        resp_[10] =  0;
        resp_[11] =  0;


        //----------------------------------------------------------------
        // Question section
        //      Note:   The question is always reiterated in the response.
        //              except when multiple questions are asked (which
        //              should never happen).  In this case, only the
        //              first question is reiterated to avoid forcing the
        //              EchoDNS server to do more work than the client.
        //              When this happens, the response claims that the
        //              client only asked 1 question, so at least the
        //              record(s) turned for that can be parsed.
        //----------------------------------------------------------------


        // QNAME

        int label_offset = 12;
        while( 1 )
        {
            int len;

            if ( (len = mesg_[label_offset]) == 0 )
            {
                resp_[label_offset] = 0;    // Terminate response QNAME
                qname_label_offset_[ qname_label_count_ ++ ] = label_offset;

                break;                     // End of QNAME
            }

            if ( (len > 63)  || ( label_offset + len >= 256+12  ) )
            {
                break;  // abort on malformed QNAME
            }

            resp_[ label_offset ] = len;
            qname_label_offset_[ qname_label_count_ ++ ] = label_offset;

            // Copy label
            //          label can't overflow resp_ due to the tests
            //          just conducted (buf flawfinder cant' see that).

            // Flawfinder: ignore
            memcpy( resp_ + label_offset + 1,  mesg_ + label_offset + 1, len);


            label_offset += len + 1;
        }

        if ( mesg_[label_offset] != 0 )
        {
            // Malformed QNAME
            resp_[3] |=  RCODE_FORMERR;

            // ANCOUNT    (no answers)
            resp_[6] =  0;              // resp_[6] should already 0
            resp_[7] =  0;

            emit_udp_packet( 12 );      // this packet will be malformed
            continue;
        }

        // If question isn't malformed, label_offset is pointing
        // at the terminating  null of the QNAME

        eoq_ = label_offset;            // eoq_:  'end of qname'


        if ( 12 >  (eoq_ - zone_length_ ))
        {
            // Malformed packet  (possibly a malicious or broken client)
            resp_[3] |=  RCODE_FORMERR;

            // ANCOUNT    (no answers)
            resp_[6] =  0;              // resp_[6] should already 0
            resp_[7] =  0;

            emit_udp_packet( 12 );      // this packet will be malformed
            continue;
        }

        // QTYPE
        //      Note: If this is   an  A  request, qtype == 1
        //            else if it's an  NS request, qtype == 2
        //
        resp_[eoq_ + 1] =  mesg_[eoq_ + 1];
        resp_[eoq_ + 2] =  mesg_[eoq_ + 2];
        unsigned short qtype = ntohs( *(unsigned short *)(mesg_ + eoq_ + 1) );



        // QCLASS
        resp_[eoq_ + 3] =  mesg_[eoq_ + 3];
        resp_[eoq_ + 4] =  mesg_[eoq_ + 4];



        // At this point, either handle A request, or the NS request
        switch ( qtype )
        {
            case RRTYPE_A:    // get IP address associated with name
                              qtype_A_handler();
                              break;

            case RRTYPE_NS:   // list of nameserver for domain
                              qtype_NS_handler();
                              break;

            case RRTYPE_SOA:  // get start of authority resource record
                              qtype_SOA_handler();
                              break;

            default:          //
                              qtype_GENERIC_handler( qtype, RCODE_NOERROR );
                              break;
        }
    }
}

//-----------------------------------------------------------------------------
// qtype_A_handler --
//      Creates UDP response, for an IPv4 name lookup  (QTYPE A)
//
//      EchoDNS assumes it will be hosted directly within its SOA:
//      For example:
//
//             If the SOA server is :               echo.localdomain.lan
//             EchoDNS expects it hosted in:    <x>.echo.localdomain.lan
//
//      More than one EchoDNS server may serve:     echo.localdomain.lan
//      For example:                            ns1.echo.localdomain.lan
//                                              ns2.echo.localdomain.lan
//
//
//      The answer that EchoDNS returns depends upon the hostlabel
//      immediately to the left of <x> (if there is one).   There are
//      five cases to handle:
//
//      [0]  No host label to the left of <x>
//           Example:   the IP address of echo.localdomain.lan
//           Return:    the IP address corresponding to the SOA itself
//
//      [1]  The client is requesting the name of an EchoDNS server
//           Examples::  ns1.echo.localdomain.lan, ns2.echo.localdomain.lan
//           Return:     address agreeing with the delegating namerver's glue
//
//      [2]  Illegal hyphen-encoded address
//           Example:   230-99999999-4000-5.echo.localdomain.lan
//           Return:    127.0.0.1
//
//      [3]  The disallowed hyphen-encoded address:  0-0-0-0
//           or an  invalid hyphen-encoded address (or none at all).
//           Example:   foo.bar.0-0-0-0.echo.localdomain.lan
//           Example:   zork.zork.zork.zork.echo.localdomain.lan
//           Return:    127.0.0.1
//
//      [4]  A valid hyphen-encoded IPv4 IP address
//           Example:   foo.bar.192-168-1-5.echo.localdomain.lan
//           Returns:   decoded IP address (e.g.:  192.168.1.5)
//
//-----------------------------------------------------------------------------
void EchoDNS_server::qtype_A_handler( )
{
    //-----------------------------------
    // Answer section
    //-----------------------------------

    // NAME - domain NAME to which resource record pertains
    //
    //        See http://www.freesoft.org/CIE/RFC/1035/43.htm
    //
    //                PTR record
    //                11000000
    resp_[eoq_ + 5] = 0xC0;
    resp_[eoq_ + 6] = 12;     // domain_offset


    // TYPE =  A record
    resp_[eoq_ + 7] =   0;
    resp_[eoq_ + 8] =   RRTYPE_A;

    // CLASS =  IN class (internet record)
    resp_[eoq_ + 9 ] =  0;
    resp_[eoq_ + 10] =  1;


    // TTL   when 0, don't cache
    //
    //        resp_[eoq_ + 11] =  127;
    //        resp_[eoq_ + 12] =  255;
    //        resp_[eoq_ + 13] =  255;
    //        resp_[eoq_ + 14] =  255;
    //

    * (unsigned int *)( resp_ + eoq_ + 11 ) = ttl_a_in_network_order_;


    // RDLENGTH   (4 bytes in answer)
    resp_[eoq_ + 15] =  0;
    resp_[eoq_ + 16] =  4;



    // Calculate index of label bearing the encoded IP to return
    // For example:
    //
    //   If:    echo.localdomain.lan
    //   then:  echo_label_index == -1
    //
    //   If:    43-3-4-5.echo.localdomain.lan
    //   then:  echo_label_index == 0
    //
    //   If:    xxx.yyy.zzz.43-3-4-5.echo.localdomain.lan
    //   then:  echo_label_index == 3
    //
    // This calculation is blind to whether the echo label
    // actually contains meaningful data.  Thus, the following
    // also true:
    //
    //   If:    xxx.echo.localdomain.lan
    //   then:  echo_label_index == 0
    //
    //   If:    xxx.yyy.zzz.echo.localdomain.lan
    //   then:  echo_label_index == 3

    int echo_label_index = qname_label_count_ - zone_labels_ -1;
    int echo_offset = 0;
    int echo_label_length = 0;
    int quad_val[4] = {0,0,0,0};

    // ptr to start of non-null terminated echo label string
    char *echo_label_ptr;

    if ( echo_label_index < 0 )   // query == value of --zone
    {
        // When asked about the IP address of
        // the domain given by --zone_a,
        // give the corresponding "zone_addr_".

        // RDATA     (the Internet address)

        resp_[eoq_ + 17] = zone_addr_[0];
        resp_[eoq_ + 18] = zone_addr_[1];
        resp_[eoq_ + 19] = zone_addr_[2];
        resp_[eoq_ + 20] = zone_addr_[3];
    }
    else
    {
        // RDATA     (the Internet address)
        //
        // EchoDNS expects that the label just before
        // the zone will be of the form:
        //
        //           number-number-number-number
        //
        // For example, if the zone is:  echo.localdomain.lan
        // EchoDNS will expect requests for name lookup of a host at
        // an IP address such as:       64.233.167.99
        // to look like this:           64-223-167-99.echo.localdomain.lan
        // or some subdomain:   moo.cow.64-223-167-99.echo.localdomain.lan
        //
        // In other words, every FQDN the client asks for gets turned
        // into an IP address by parsing the hyphenated label just
        // before the nameserver's authoritative zone
        // (i.e.: echo.localdomain.lan)

        echo_offset = qname_label_offset_[ echo_label_index ];

        unsigned char *p = resp_ + echo_offset;
        echo_label_length = (int) (*p);
        echo_label_ptr    = (char *) p+1;


        int current_quad;

        for (int i=0; i <= echo_label_length; i++)
        {
            for (current_quad = 0; current_quad< 4; current_quad++)
            {
                // Skip over any leading crud
                while( ! isdigit( p[i] ) && i < echo_label_length ) { i++; }
                if (i > echo_label_length ) { break; }

                // compute quad value
                while( isdigit( p[i] ) && i <= echo_label_length )
                {
                    quad_val[current_quad] =
                        (quad_val[current_quad] * 10) + p[i] - '0';

                    i++;
                }

                if ( quad_val[current_quad] > 255 ) { break; }
            }
        }

        if (current_quad == 4 )  // success
        {
            resp_[eoq_ + 17] =  quad_val[0];
            resp_[eoq_ + 18] =  quad_val[1];
            resp_[eoq_ + 19] =  quad_val[2];
            resp_[eoq_ + 20] =  quad_val[3];
        }
        else
        {
            resp_[eoq_ + 17] =  127;
            resp_[eoq_ + 18] =  0;
            resp_[eoq_ + 19] =  0;
            resp_[eoq_ + 20] =  1;
        }
    }

    // The four possible cases that remain at this point are:
    //
    //      [1]  The client is requsting the name of an EchoDNS
    //           server.   Send back data that will agree with
    //           the IP address used by the delegating nameserver
    //           (i.e.: --ipaddr <dottedquad>).
    //
    //      [2]  An illegal address with was coerced into 127.0.0.1
    //
    //      [3]  All values within the dotted quad are 0.
    //           This is the reserved "default network" address,
    //           and is not a valid response in an A record.
    //           Coerce it 127.0.0.1 also.
    //
    //      [4]  A properly hyphen-encoded IP address
    //           was parsed and resolved to legal value.
    //

    bool found_ns_name = false;

    if ( echo_offset > 0 )
    {
        for (int i=0; i< ns_host_count_; i++)
        {
            if ( (echo_label_length == ns_host_name_length_[i]) &&
                ! strncasecmp( ns_host_name_[i], echo_label_ptr,
                               ns_host_name_length_[i]
                             )
               )
            {
                // This is the IP address of a nameserver within the zone
                // specified via --ns_a (or -n).
                //
                // Note:  this answer must agree with the one provided
                //        by the delegating nameserver, or else EchoDNS
                //        will poision the cache of those who ask
                //        EchoDNS directly for its own hostname.

                resp_[eoq_ + 17] =  ns_host_addr_[i][0];
                resp_[eoq_ + 18] =  ns_host_addr_[i][1];
                resp_[eoq_ + 19] =  ns_host_addr_[i][2];
                resp_[eoq_ + 20] =  ns_host_addr_[i][3];

                found_ns_name = true;
                break;
            }
        }
    }

    if ( ! found_ns_name  && (echo_label_index >= 0) &&
         ! memcmp( zero_quad, quad_val, sizeof( zero_quad ))
       )
    {
        // This isn't the --zone_a name itself,
        // so we didn't skip the dotted quad computation;
        // however the dotted quad ended up with the value:
        // 0.0.0.0,  which is the reserved network address.
        //
        // Return 127.0.0.1 instead of 0.0.0.0

        resp_[eoq_ + 17] =  127;
        resp_[eoq_ + 18] =  0;
        resp_[eoq_ + 19] =  0;
        resp_[eoq_ + 20] =  1;
    }

    int size = eoq_ + 21;

    emit_udp_packet( size );

    if ( log_requests_ )
    {
        // It's ok to trash resp now, because the packet has been sent.
        // Make it easy to print by turning label length bytes into '.'

        for (int tmp=1; tmp< qname_label_count_ -1; tmp++)
        {
            resp_[ qname_label_offset_[ tmp ] ] = '.';
        }
        // The NAME is already null-terminated.


        fprintf( stdout, "%3d %3d %3d %3d     %-16s  %s\n",
                 resp_[size-4], resp_[size-3], resp_[size-2], resp_[size-1],
                 inet_ntoa(  (( sockaddr_in) client_addr_).sin_addr ),
                 (resp_ + qname_label_offset_[ 0 ]  + 1)
               );

        fflush(stdout);
    }
}


//-----------------------------------------------------------------------------
// qtype_NS_handler --
//      Respond to an NS query by handing back the nameservers that serve
//      the zone delegated to EchoDNS (i.e.: the "listed servers").
//      Glue records for the "listed servers" are also returned in
//      the response.
//
//      For example, suppose 3 EchoDNS servers are authoritative
//      for the domain  "echo.localdomain.lan":
//
//         ns1.echo.localdomain.lan. 86400  IN   A   192.168.1.97
//         ns2.echo.localdomain.lan. 86400  IN   A   192.168.1.98
//         ns3.echo.localdomain.lan. 86400  IN   A   192.168.1.99
//
//      Suppose we ask ns3 to show us all the EchoDNS nameservers
//      for echo.localdomain.lan like this:
//
//         % dig @192.168.1.99   echo.localdomain.lan. ns
//
//      What 'dig' will report back is:
//
//         ; <<>> DiG 9.3.2 <<>> @192.168.1.99 echo.localdomain.lan. ns
//         ; (1 server found)
//         ;; global options:  printcmd
//         ;; Got answer:
//         ;; ->>HEADER<<- opcode: QUERY, status: NOERROR, id: 19251
//         ;; flags: qr aa rd; QUERY: 1, ANSWER: 3, AUTHORITY: 0, ADDITIONAL: 3
//
//         ;; QUESTION SECTION:
//         ;echo.localdomain.lan.           IN   NS
//
//         ;; ANSWER SECTION:
//         echo.localdomain.lan.     172800  IN   NS  ns1.echo.localdomain.lan.
//         echo.localdomain.lan.     172800  IN   NS  ns2.echo.localdomain.lan.
//         echo.localdomain.lan.     172800  IN   NS  ns3.echo.localdomain.lan.
//
//         ;; ADDITIONAL SECTION:
//         ns1.echo.localdomain.lan. 172800  IN   A   192.168.1.97
//         ns2.echo.localdomain.lan. 172800  IN   A   192.168.1.98
//         ns3.echo.localdomain.lan. 172800  IN   A   192.168.1.99
//
//         ;; Query time: 1 msec
//         ;; SERVER: 192.168.1.99#53(192.168.1.99)
//         ;; WHEN: Sat Feb 24 20:31:58 2007
//         ;; MSG SIZE  rcvd: 150
//
//-----------------------------------------------------------------------------
void EchoDNS_server::qtype_NS_handler()
{
    if ( ! query_equals_zone_name() )
    {
        // QNAME does not exist
        resp_[3] |=  RCODE_NXDOMAIN;

        // ANCOUNT    (no answers)
        resp_[6] =  0;              // resp_[6] should already 0
        resp_[7] =  0;


        emit_udp_packet( eoq_ + 5 );
        return;
    }


    // ANCOUNT
    //          Nameserver names
    *((unsigned short *) & resp_[6]) = htons( ns_host_count_ );

    // NSCOUNT
    //          EchoDNS is recursively authoratiative (no delegations)
    resp_[8] = 0;
    resp_[9] = 0;


    // ARCOUNT
    //          Glue records
    *((unsigned short *) & resp_[10]) = htons( ns_host_count_ );


    // Create Answer resource records
    int offset = eoq_ + 5;


    for (int i=0; i< ns_host_count_; i++)
    {
        // NAME
        //                  PTR record
        //                  11000000
        resp_[ offset++ ] = 0xC0;
        resp_[ offset++ ] = 12;   // zone is in QNAME, after 12 byte DNS header


        // TYPE
        //
        resp_[ offset++ ] = 0;
        resp_[ offset++ ] = RRTYPE_NS;

        // CLASS
        //
        resp_[ offset++ ] = 0;
        resp_[ offset++ ] = 0x1;  // IN


        // TTL
        //
        * (unsigned int *)( & resp_[ offset ] ) = ttl_ns_a_in_network_order_;
        offset +=4;


        // RDLENGTH
        //      1 byte for size of NS label
        //    + ns_host_name_length_[i]
        //    + 1 byte for PTR marker C0
        //    + 1 byte for PTR itself
        //

        *((unsigned short*)&resp_[offset]) = htons(3+ ns_host_name_length_[i]);
        offset +=2;


        // Size of EchoDNS host label
        resp_[offset++]  = ns_host_name_length_[i];


        // Copy EchoDNS host label itself
        //      This cannot overflow resp_ because the length of an NS
        //      lookup was lovingly calculated within process_requests()
        //      prior to startup;  we wouldn't be here unless the next
        //      line was guaranteed to be safe.

        // Flawfinder: ignore
        memcpy( & resp_[offset], ns_host_name_[i], ns_host_name_length_[i] );
        offset +=  ns_host_name_length_[i];

        // The trailing part of the NS in this answer is just
        // the zone for which EchoDNS is authoratiative.
        // This is located just after the DNS header.
        // We can just point to it.
        //
        //                   PTR record
        //                   11000000
        resp_[ offset++ ] =  0xC0;
        resp_[ offset++ ] =  12;        // zone is in QNAME (after DNS header)
    }


    //---------------------------------------
    // Additional section
    //          Glue records for nameservers
    //---------------------------------------

    for (int i=0; i< ns_host_count_; i++)
    {
        // NAME
        //      It should be possible to compress the names in the
        //      additional by passing back pointers to names in
        //      the answer section; however, each name in the answer
        //      section contains a pointer to the name in the question.
        //      Resolvers should be able to handle pointers to pointers
        //      but the savings gained by relying on them to do so is
        //      very small (nameserver labels are typically 3 chars fewer),
        //      and the risk of encountering a resolver that's buggy is
        //      unknown.  Therefore, I'm erring on the side of caution
        //      by only assuming the resolver can follow a single level
        //      of indirection.   The only new risk this introduces is
        //      that if you have a huge number of longish nameserver
        //      names you might go over the 521 byte UDP limit.  Given
        //      how easily non-AXFR responses fit within 512 bytes,
        //      that seems far-fetched (hence, a lesser concern).


        // Size of EchoDNS host label
        resp_[offset++]  = ns_host_name_length_[i];

        // Copy EchoDNS host label itself
        //      This cannot overflow resp_ because the length of an NS
        //      lookup was lovingly calculated within process_requests()
        //      prior to startup;  we wouldn't be here unless the next
        //      line was guaranteed to be safe.

        // Flawfinder: ignore
        memcpy( &resp_[offset], ns_host_name_[i], ns_host_name_length_[i] );

        offset +=  ns_host_name_length_[i];

        // The trailing part of the NS in this answer is just
        // the zone for which EchoDNS is authoritative.
        // This is located just after the DNS header.
        // We can just point to it.
        //
        //                  PTR record
        //                  11000000
        resp_[ offset++ ] = 0xC0;
        resp_[ offset++ ] = 12;   // zone is in QNAME (after DNS header)


        // TYPE
        //
        resp_[ offset++ ] = 0;
        resp_[ offset++ ] = 0x1;  // A record

        // CLASS
        //
        resp_[ offset++ ] = 0;
        resp_[ offset++ ] = 0x1;  // IN


        // TTL
        //
        * (unsigned int *)( & resp_[offset] ) = ttl_ns_a_in_network_order_;
        offset +=4;


        // RDLENGTH   (4 bytes in answer)
        resp_[ offset++ ] =  0;
        resp_[ offset++ ] =  4;


        // RDATA
        resp_[ offset++ ] =  ns_host_addr_[i][0];
        resp_[ offset++ ] =  ns_host_addr_[i][1];
        resp_[ offset++ ] =  ns_host_addr_[i][2];
        resp_[ offset++ ] =  ns_host_addr_[i][3];
    }

    emit_udp_packet( offset  );

    if ( log_requests_ )
    {
        // It's ok to trash resp now, because the packet has been sent.
        // Make it easy to print by turning label length bytes into '.'

        for (int tmp=1; tmp< qname_label_count_ -1; tmp++)
        {
            resp_[ qname_label_offset_[ tmp ] ] = '.';
        }
        // The NAME is already null-terminated.

        fprintf( stdout, "QTYPE        NS     %-16s  %s\n",
                 inet_ntoa(  (( sockaddr_in) client_addr_).sin_addr ),
                 (resp_ + qname_label_offset_[ 0 ]  + 1)
               );

        fflush(stdout);
    }
}

//-----------------------------------------------------------------------------
// qtype_SOA_handler --
//      Returns an SOA record for the zone specified by the --zone_a
//      command line argument.
//
//      The RDATA section of a SOA answer looks like this:
//
//
//      Bit:        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15
//                +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//                /                                               /
//                /                     MNAME      (variable)     /
//                /                                               /
//                +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//                /                                               /
//                /                     RNAME      (variable)     /
//                /                                               /
//                +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//                |                                               |
//                |.................... SERIAL ....(4 bytes)......|
//                |                                               |
//                +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//                |                                               |
//                |.................... REFRESH ...(4 bytes)......|
//                |                                               |
//                +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//                |                                               |
//                |.................... RETRY .....(4 bytes)......|
//                |                                               |
//                +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//                |                                               |
//                |.................... EXPIRE ....(4 bytes)......|
//                |                                               |
//                +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//                |                                               |
//                |.................... MINIMUM ...(4 bytes)......|
//                |                                               |
//                +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//
//      Where:
//
//      MNAME      Primary NS
//                 The <domain-name> of the name server that was the
//                 original or primary source of data for this zone.
//
//      RNAME      Responsible Person
//                 A <domain-name> which specifies the mailbox of the person
//                 responsible for this zone.  RFC1912 2.2 recommends:
//                 hostmaster.<domain-name>
//
//      SERIAL     Serial number for zone info.
//                 The unsigned 32 bit version number of the original copy
//                 of the zone.  Zone transfers preserve this value.  This
//                 value wraps and should be compared using sequence space
//                 arithmetic.  RFC1912 2.2 recommends:  YYYYMMDDnn
//
//      REFRESH    A 32 bit time interval before the zone should be checked
//                 for updates by slave nameservers.  Supposedly, an hour or
//                 two is a good value, but this is all moot for EchoDNS.
//
//      RETRY      A 32 bit time interval that should elapse before a
//                 failed refresh from a slave should be retried.
//
//      EXPIRE     A 32 bit time value that specifies the upper limit on
//                 the time interval that can elapse before the zone is no
//                 longer authoritative.  The value musr be greater than the
//                 minimum and retry intervals.  RFC1912 2.2 recommends
//                 something in the range of 2-4 weeks.
//
//      MINIMUM    The unsigned 32 bit minimum TTL field that should be
//                 exported with any RR from this zone.
//
//      Most of these fields are pertinent only for name server maintenance
//      operations.  However, MINIMUM is used in all query operations that
//      retrieve RRs from a zone.  Whenever a RR is sent in a response to a
//      query, the TTL field is set to the maximum of the TTL field from
//      the RR and the MINIMUM field in the appropriate SOA.  Thus MINIMUM
//      is a lower bound on the TTL field for all RRs in a zone.  Note that
//      this use of MINIMUM should occur when the RRs are copied into the
//      response and not when the zone is loaded from a master file or via
//      a zone transfer.  The reason for this provision is to allow future
//      dynamic update facilities to change the SOA RR with known semantics.
//
//
//                               RFC 1912,2308       TinyDNS default
//                               -----------------   --------------
//                Refresh:       1200    -   43200        16384
//                Retry:         120     -    7200         2048
//                Expire:        1209600 - 2419200      1048576
//                Minimum TTL:   3600    -   86400         2560
//
//-----------------------------------------------------------------------------
void EchoDNS_server::qtype_SOA_handler()
{
    if ( ! query_equals_zone_name() )
    {
        qtype_GENERIC_handler(  RRTYPE_SOA, RCODE_NOERROR);
        return;
    }

    // Create Answer resource records
    int offset = eoq_ + 5;


    // NAME - domain NAME to which resource record pertains
    //
    //                    PTR record
    //                    11000000
    resp_[ offset ++ ] =  0xC0;
    resp_[ offset ++ ] =  12;     // domain_offset

    // TYPE =  SOA record
    resp_[ offset ++ ] =  0;
    resp_[ offset ++ ] =  RRTYPE_SOA;


    // CLASS =  IN class (internet record)
    resp_[ offset ++ ] =  0;
    resp_[ offset ++ ] =  1;

    // TTL
    * (unsigned int *)(resp_ + offset) = soa_ttl_minimum_in_network_order_;
    offset += 4;


    // RDLENGTH
    //          This is easier to fill in RDLENGTH at the bottom of this func,
    //          when we know what the value actually is.  Sure, it could be
    //          precomputed exactly once, but the code would look a bit too
    //          cryptic... just to save a single subtraction.  Pointless.


    int rdlength_offset = offset;

    // resp_[ offset ++ ] = (deferred)
    // resp_[ offset ++ ] = (deferred)

    offset +=2;


    //------------------------------
    // RDATA
    //          The SOA data itself
    //------------------------------

    //-------------------------------------------
    // MNAME:  primary dns server for this domain
    //-------------------------------------------

    // Size of initial EchoDNS host label
    resp_[ offset ++ ] = ns_host_name_length_[0];


    // Copy initial EchoDNS host label itself
    // Flawfinder: ignore
    memcpy( & resp_[offset], ns_host_name_[0], ns_host_name_length_[0] );
    offset +=  ns_host_name_length_[0];

    // The trailing part of the NS in this answer is just
    // the zone for which EchoDNS is authoritative.
    // This is located just after the DNS header.
    // We can just point to it.
    //
    //                   PTR record
    //                   11000000
    resp_[ offset++ ] =  0xC0;
    resp_[ offset++ ] =  12;        // zone is in QNAME (after DNS header)


    //---------------------------
    // RNAME:  responsible person
    //---------------------------

    resp_[ offset++ ] = sizeof("hostmaster") - 1;   // not counting the null

    // Copy responsible person's name
    // Flawfinder: ignore
    memcpy( & resp_[offset], "hostmaster" ,  sizeof("hostmaster") - 1);
    offset +=  sizeof("hostmaster") - 1;


    // The trailing part of the NS in this answer is just
    // the zone for which EchoDNS is authoratiative.
    // This is located just after the DNS header.
    // We can just point to it.
    //
    //                   PTR record
    //                   11000000
    resp_[ offset++ ] =  0xC0;
    resp_[ offset++ ] =  12;        // zone is in QNAME (after DNS header)


    //-------
    // SERIAL
    //-------
    * (unsigned int *)( & resp_[offset] ) = soa_serial_in_network_order_;
    offset +=4;

    //--------
    // REFRESH
    //--------
    * (unsigned int *)( & resp_[offset] ) = soa_ttl_refresh_in_network_order_;
    offset +=4;

    //------
    // RETRY
    //------
    * (unsigned int *)( & resp_[offset] ) = soa_ttl_retry_in_network_order_;
    offset +=4;

    //-------
    // EXPIRE
    //-------
    * (unsigned int *)( & resp_[offset] ) = soa_ttl_expire_in_network_order_;
    offset +=4;

    //--------
    // MINIMUM
    //--------
    * (unsigned int *)( & resp_[offset] ) = soa_ttl_minimum_in_network_order_;
    offset +=4;


    // RDLENGTH
    * (unsigned short *)(& resp_[ rdlength_offset ]) =
         htons(offset - rdlength_offset - 2);

    emit_udp_packet( offset );

    if ( log_requests_ )
    {
        // It's ok to trash resp now, because the packet has been sent.
        // Make it easy to print by turning label length bytes into '.'

        for (int tmp=1; tmp< qname_label_count_ -1; tmp++)
        {
            resp_[ qname_label_offset_[ tmp ] ] = '.';
        }
        // The NAME is already null-terminated.

        fprintf( stdout, "QTYPE       SOA     %-16s  %s\n",
                 inet_ntoa(  (( sockaddr_in) client_addr_).sin_addr ),
                 (resp_ + qname_label_offset_[ 0 ]  + 1)
               );

        fflush(stdout);
    }
}


//-----------------------------------------------------------------------------
// qtype_GENERIC_handler --
//      Rather than leave a client hanging around for an answer and/or
//      sending retries, this handler sends back the specified status
//      code along with an empty answer section.
//
//-----------------------------------------------------------------------------
void EchoDNS_server::qtype_GENERIC_handler(unsigned short qtype, Rcode status)
{
    resp_[3] |= status;

    // ANCOUNT
    //      This is set to 0 later there's an error,
    //      or to  ns_host_count_in_network_order
    //      for NS records (there may be more than 1).
    //
    resp_[6] =  0;
    resp_[7] =  0;

    emit_udp_packet( eoq_ + 5 );

    if ( log_requests_ )
    {
        // It's ok to trash resp now, because the packet has been sent.
        // Make it easy to print by turning label length bytes into '.'

        for (int tmp=1; tmp< qname_label_count_ -1; tmp++)
        {
            resp_[ qname_label_offset_[ tmp ] ] = '.';
        }
        // The NAME is already null-terminated.

        fprintf( stdout, "QTYPE       %3d     %-16s  %s\n",
                 (int) qtype,
                 inet_ntoa(  (( sockaddr_in) client_addr_).sin_addr ),
                 (resp_ + qname_label_offset_[ 0 ]  + 1)
               );

        fflush(stdout);
    }
}

//-----------------------------------------------------------------------------
// query_equals_zone_name --
//      Returns true if and only if the query matches the name specified by
//      the --zone_a flag  (the comparison is a case-insensitive match).
//
//-----------------------------------------------------------------------------
bool  EchoDNS_server::query_equals_zone_name()
{
    const char *zone_label_ptr  = (const char *) zone_name_;
    const char *query_label_ptr = (const char *) &resp_[12];

    // See if NS lookup query is for EchoDNS's authoritative zone

    int zone_label_length;
    while ( (zone_label_length = *zone_label_ptr) != 0 )
    {
        if ( *query_label_ptr != zone_label_length  ||
              strncasecmp( query_label_ptr + 1,
                           zone_label_ptr  + 1,
                           zone_label_length
                         )
           )
        {
            break;
        }
        zone_label_ptr  += zone_label_length + 1;
        query_label_ptr += zone_label_length + 1;

    }

    return ( ! *zone_label_ptr &&  ! *query_label_ptr );
}



//-----------------------------------------------------------------------------
// emit_udp_packet --
//       Writes a UDP packet to the cient
//-----------------------------------------------------------------------------
void EchoDNS_server::emit_udp_packet( int size )
{
    if ( debug_server_ )
    {
        fprintf(stdout, "sendto args:\n"
               "   udp_sock: %d\n"
               "   resp: \n", udp_sock_);

        for (int i=0; i< size; i++)
        {
            fprintf(stdout," %d", (int) resp_[i]);
        }
        fprintf(stdout,"\n");
        fflush(stdout);
    }

    int status = sendto( udp_sock_,
                         resp_,
                         size,
                         0,
                         (sockaddr *) & client_addr_,
                         sizeof_client_addr_
                       );

    if (status != size)
    {
        perror("socket()");
        fprintf(ofile_, "sendto(): short write (%d).\n", status);
        // exit(1);
    }
}


//-----------------------------------------------------------------------------
// get_bind_quad --
//      Fetch IPv4 address EchoDNS is listening on as a string
//      in "dotted quad" notation.
//-----------------------------------------------------------------------------
const char * EchoDNS_server::get_bind_quad()
{
    return bind_quad_;
}

//-----------------------------------------------------------------------------
// get_zone_fqdn --
//      Fetch zone that EchoDNS has been delegated.
//      This domain name will always end in a '.' character.
//
//      For example:   "echo.localdomain.lan."
//
//-----------------------------------------------------------------------------
const char * EchoDNS_server::get_zone_fqdn()
{
    return zone_fqdn_;
}

//-----------------------------------------------------------------------------
// get_ns_host_count --
//      Fetch the number of nameservers that service the domain that have been
//      delegated to EchoDNS.  For example, suppose EchoDNS is authoratiative
//      for echo.localdomain.lan, and you ran it like this:
//
//            echodns                                             \
//               --user     nobody                                \
//               --serial   2007022601                            \
//               --bind     192.168.1.98                          \
//               --zone_a   echo.localdomain.lan  192.168.1.99    \
//               --ns_a     ns1                     192.168.1.97  \
//               --ns_a     ns2                     192.168.1.98  \
//               --ns_a     ns3                     192.168.1.99  \
//               --log
//
//      The value  3  would be return by this function because ns1, ns2,
//      and ns3 service echo.localdomain.lan.
//
//-----------------------------------------------------------------------------
int EchoDNS_server::get_ns_host_count()
{
    return ns_host_count_;
}


//-----------------------------------------------------------------------------
// add_ns_a --
//      Register a nameserver that services the domain that EchoDNS
//      has been delegated.  All nameservers specified via the command
//      line flag '--ns_a' are registered using this function;  this
//      enables EchoDNS to respond properly to 'NS' requests and to
//      requests for the 'A' record of each EchoDNS server  (note:
//      the answers returned must match the values provied by glue
//      records in the delegating zone).
//
// REQUIRES
//      The nameserver label 'ns_label' must live directly within the
//      zone for which EchoDNS is authoritative.
//
//-----------------------------------------------------------------------------
void EchoDNS_server::add_ns_a( const char *ns_label, const char *ns_quad)
{
    if (ns_host_count_ >= MAX_NS_ZONE_COUNT)
    {
        fprintf(ofile_,
                "ERROR: %s Too many namservers in zone (max is %d)\n",
                 prog_name_, MAX_NS_ZONE_COUNT);

        exit(1);
    }

    int ns_label_length  = strlen( ns_label );          // Flawfinder: ignore

    if ( ns_label_length > (sizeof(ns_host_name_[0]) -1 ) )
    {
        fprintf(ofile_,
                 "ERROR: %s : The --ns_a host label '%s' too long (%d > %d)\n",
                  prog_name_,  ns_label, ns_label_length,
                  sizeof(ns_host_name_[0]) -1);

        exit(1);
    }

    if ( ! ns_label_length )
    {
        fprintf(ofile_,
                "ERROR: %s : No --ns_a host label can have length 0.\n",
                prog_name_);

        exit(1);
    }

    for (const char *p = ns_label; *p; p++)
    {
        if ( !isalpha( *p ) &&  !isdigit( *p ) && *p != '-')
        {
            fprintf(ofile_,
                    "ERROR: %s : The --ns_a host label '%s' cannot contain "
                    "character: '%c'\n",
                    prog_name_, ns_label, *p);
            exit(1);
        }
    }

    if ( '-' == ns_label[0]  || '-' == ns_label[ ns_label_length -1 ])
    {
        fprintf(ofile_,
                "ERROR: %s : The --ns_a host label '%s' cannot begin "
                "or end with: '-'\n",
                prog_name_, ns_label);
        exit(1);
    }

    // Flawfinder: ignore
    strncpy(ns_host_name_[ ns_host_count_ ] , ns_label, ns_label_length + 1 );

    ns_host_name_length_[ ns_host_count_ ] = ns_label_length;
    set_ipaddr_array( ns_host_addr_[ ns_host_count_ ],  ns_quad );
    ns_host_count_++;
}


//-----------------------------------------------------------------------------
// set_zone_a --
//      Sets the FQDN and IP address of the zone for which EchoDNS
//      has been delegated authority.  Note:  the answer returned must
//      match the value provided by the delegating zone.
//
//-----------------------------------------------------------------------------
void  EchoDNS_server::set_zone_a(const char *z_name, const char *z_quad)
{
    if ( zone_fqdn_[0] )
    {
        fprintf(ofile_, "ERROR: %s The --zone_a (or -z) argument "
                        "cannot appear more than once.\n",
                        prog_name_);
        exit(1);
    }

    // Flawfinder: ignore
    if ( (zone_length_ = strlen( z_name ))  >= (sizeof(zone_fqdn_) -2 ) )
    {
         fprintf(ofile_,
                 "ERROR: %s : domain too long (%d >= %d)\n",
                  prog_name_, zone_length_, sizeof(zone_fqdn_)-2);
        exit(1);
    }


    // Make sure the zone_fqdn_ ends in a '.'

    memcpy(zone_fqdn_, z_name, zone_length_ +1 );          // Flawfinder: ignore
    if ( zone_fqdn_[ zone_length_ -1 ] != '.' )
    {
        zone_fqdn_[ zone_length_    ] = '.';
        zone_fqdn_[ zone_length_ + 1] = 0;
        zone_length_ ++;
    }

    // Put zone_fqdn_ into UDP "name/qname"  format
    // to make NS lookups fast

    memcpy(zone_name_ + 1, zone_fqdn_, zone_length_ + 1);  // Flawfinder: ignore
    unsigned char *lhead = zone_name_;
    unsigned char *ltail = zone_name_ + 1;

    while (1)  // zone_name_:  length,label, length,label, ..., 0
    {
        while  ( *ltail && *ltail != '.')        { ltail ++; }
        if ( ! ( *lhead =  ltail - lhead - 1) )  { break ;}
        lhead = ltail;
        ltail ++;
    }

    set_ipaddr_array( zone_addr_, z_quad );
    for (char *p = zone_fqdn_; p = strchr(p,'.'); zone_labels_ ++) {p++;}
}


//-----------------------------------------------------------------------------
// set_port --
//      Set the port number on which EchoDNS listens.
//      The default value is 53.
//-----------------------------------------------------------------------------
void  EchoDNS_server::set_port(int port)
{
    if ( (port < 0)  ||  (port > 65535))
    {
        fprintf(ofile_,
                "ERROR: %s :  port must be in the range [0,65535]\n",
                prog_name_);

        exit(1);
    }
    server_port_ = port;
}


//-----------------------------------------------------------------------------
// set_ipv4_bind_address --
//      Set the IP address on which EchoDNS will bind.
//      Note:  you must not run EchoDNS on an IP address that is already
//      in use by another program (e.g.: tinydns, BIND).
//
//-----------------------------------------------------------------------------
void EchoDNS_server::set_ipv4_bind_address( const char *dotted_quad )
{
    // TODO:  be stricter about dotted quad here.
    int len  =  strlen( dotted_quad );          // Flawfinder: ignore

    if ( len >= sizeof( bind_quad_ ) )
    {
        fprintf(ofile_,
                "ERROR:  %s in set_ipv4_address(%s)\n",
                prog_name_, dotted_quad, "Address too long");

        exit(1);
    }

    strncpy(bind_quad_, dotted_quad, len);      // Flawfinder: ignore
    bind_quad_[len] = 0;
}


//-----------------------------------------------------------------------------
// set_ttl_a --
//      Set the time to live (TTL) of normal "A" resource records
//      returned by EchoDNS.
//-----------------------------------------------------------------------------
void EchoDNS_server::set_ttl_a(int ttl)
{
    if ( ttl < 0) { ttl = 0;}
    ttl_a_in_network_order_ = htonl( ttl );
}

//-----------------------------------------------------------------------------
// set_ttl_ns_a --
//      Set the TTL of  "A" records that correspond to
//      EchoDNS nameserver names.
//-----------------------------------------------------------------------------
void EchoDNS_server::set_ttl_ns_a(int ttl)
{
    if ( ttl < 0) { ttl = 0;}
    ttl_ns_a_in_network_order_ = htonl( ttl );
}

//-----------------------------------------------------------------------------
// set_effective_user --
//      Set the name of the user that EchoDNS should run as after
//      binding to its port.
//-----------------------------------------------------------------------------
void EchoDNS_server::set_effective_user(const char *p)
{
    int name_length = strlen(p);                        // Flawfinder: ignore

    if ( name_length >= EFFECTIVE_USER_BUF_SIZE )
    {
        fprintf(ofile_,
           "ERROR: %s:  User specifed by --user flag must be < %d chars\n",
                        prog_name_, EFFECTIVE_USER_BUF_SIZE );
        exit(1);
    }

    strncpy(effective_user_, p, name_length + 1);       // Flawfinder: ignore
}



//-----------------------------------------------------------------------------
// set_ipaddr_array --
//      Packs 4-element array (ip_quad) with values parsed from
//      IPv4 address string dotted_quad
//-----------------------------------------------------------------------------
void EchoDNS_server::set_ipaddr_array(int ip_quad[4], const char *dotted_quad)
{
    const char *p = dotted_quad;

    memset(ip_quad, 0, sizeof(int) * 4);

    for (int i=0; *p && i<4; i++, p++)
    {
        while( isdigit( *p ) )
        {
            ip_quad[i] = ip_quad[i] * 10 + (*p - '0');
            p++;
        }

        if ( ip_quad[i] > 255  || ip_quad[i] < 0 )
        {
            fprintf(ofile_,
                    "ERROR: \n"
                    "  Illegal IP address:\n\n", dotted_quad);
            exit(1);
        }
    }
}

void EchoDNS_server::set_soa_serial(unsigned int soa_serial)
{
    soa_serial_in_network_order_ = htonl( soa_serial );
}




//-----------------------------------------------------------------------------
// emit_help --
//      Emit help message and exit with a non-zero status code.
//-----------------------------------------------------------------------------
void emit_help(char *appname)
{
            printf("\n"
 "OVERVIEW\n"
 "   EchoDNS provides a wildcard DNS domain for all IPv4 Internet addresses;\n"
 "   it does this by inferring the IP address to return from the host label\n"
 "   immediately to the left of the zone for which it is authoritative.\n"
 "\n"
 "   This IP-bearing host label is expected to contain digits separated\n"
 "   by hyphens (in reality, any non-digit will do, but hyphens are\n"
 "   used in all examples by convention).\n"
 "\n"
 "   For example, suppose EchoDNS is authoritative for\n"
 "   the zone  'echo.localdomain.lan', and it is asked\n"
 "   to translate the following FQDN into an IP address:\n"
 "\n"
 "        moo.cow.192-168-1-5.echo.localdomain.lan\n"
 "\n"
 "   EchoDNS returns:   192.168.1.5\n"
 "\n\n"
 "SYNTAX\n"
 "    %s \\ \n"
 "    [ {-h | --help   }              ]   (print this help message)\n"
 "    [ {-v | --version}              ]   (print version string)\n"
 "    [ {-l | --log    }              ]   (log src/dest IP to stdout)\n"
 "    [ {-d | --debug  }              ]   (emit debug messages)\n"
 "    [ {-t | --ttl    } <seconds>    ]   ('A'  record TTL "
                                        "default: " DEFAULT_A_RR_TTL ")\n"
 "    [ {-T | --TTL    } <seconds>    ]   ('NS' record TTL "
                                        "default: " DEFAULT_NS_A_RR_TTL ")\n"
 "    [ {-u | --user   } <username>   ]   (run as after binding to port)\n"
 "    [ {-s | --serial } <int-value>  ]   (SOA serial number: YYYYMMDDXX)\n"
 "    [ {-b | --bind   } <addr>       ]   (network address to listen on)\n"
 "    [ {-p | --port   } <portnum>    ]   (port to listen on default: 53)\n"
 "      {-z | --zone_a } <name> <addr>    (zone name and addr zone fqdn)\n"
 "    [ {-n | --ns_a   } <name> <addr>]+  (nameserver name in zone & addr)\n"
 "\n\n"
 "USAGE\n"
 "  The following command binds an instance of EchoDNS on 192.168.1.98\n"
 "  using the default port 53.  It is authoritative for the zone\n"
 "  'echo.localdomain.lan'.\n"
 "\n"
 "  Three EchoDNS servers are listed for this zone ('ns1','ns2', and 'ns3')\n"
 "  which are bound to the IP addresses 192.168.1.9[7-9], respectively.\n"
 "  The FQDN of these nameservers will be ns[1-3].echo.localdomain.lan.\n"
 "  You must not use any dots within their names when you specify them\n"
 "  via the  --ns_a flag  because they must be *directly* contained within\n"
 "  the zone indicated by --zone_a  (e.g.:  'echo.localdomain.lan').\n"
 "\n"
 "  The canonical IP address for the zone itself is 192.168.1.99, and\n"
 "  the serial number of the SOA is 2007022601  (Feb 26, 2007, version 1).\n"
 "\n"
 "  Here's how to run EchoDNS from the command line:\n"
 "\n"
 "    %s \\\n"
 "        --user   nobody                            \\\n"
 "        --serial 2007022601                        \\\n"
 "        --bind   192.168.1.98                      \\\n"
 "        --zone_a echo.localdomain.lan 192.168.1.99 \\\n"
 "        --ns_a   ns1                  192.168.1.97 \\\n"
 "        --ns_a   ns2                  192.168.1.98 \\\n"
 "        --ns_a   ns3                  192.168.1.99 \\\n"
 "        --log\n"
 "\n"
 "  Ideally, you should run EchoDNS using something like daemontools,\n"
 "  (see:  http://cr.yp.to/daemontools.html for details), and maintain logs\n"
 "  using multilog (see: http://cr.yp.to/daemontools/multilog.html).\n"
 "\n"
 "\n",
                appname,appname);
            exit(1);
}

//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------

int main(int argc, char *argv[])
{
    EchoDNS_server  svr(argv[0], stderr);

    for(int i=1; i<argc; i++)
    {
        //---------------------
        // 0-arg flags
        //---------------------

        if ( !strcmp(argv[i],"-h") ||!strcmp(argv[i],"--help"))
        {
            emit_help(argv[0]);
        }

        if ( !strcmp(argv[i],"-v") ||!strcmp(argv[i],"--version"))
        {
            fprintf(stdout,"%s\n", Version);
            exit(1);
        }

        if ( !strcmp(argv[i],"-l") ||!strcmp(argv[i],"--log"))
        {
            svr.set_log_requests(true);
            continue;
        }

        if ( !strcmp(argv[i],"-d") ||!strcmp(argv[i],"--debug"))
        {
            svr.set_debug_server(true);
            continue;
        }

        //---------------------
        // 1-arg flags
        //---------------------

        if (i+1 < argc)
        {
            if ( !strcmp(argv[i],"-t") ||!strcmp(argv[i],"--ttl"))
            {
                svr.set_ttl_a( atoi( argv[i+1] ));      // Flawfinder: ignore
                i++; continue;
            }

            if ( !strcmp(argv[i],"-T") ||!strcmp(argv[i],"--TTL"))
            {
                svr.set_ttl_ns_a( atoi( argv[i+1] ));   // Flawfinder: ignore
                i++; continue;
            }

            if ( !strcmp(argv[i],"-u") ||!strcmp(argv[i],"--user"))
            {
                svr.set_effective_user( argv[i+1] );
                i++; continue;
            }

            if ( !strcmp(argv[i],"-s") ||!strcmp(argv[i],"--serial"))
            {
                svr.set_soa_serial( atoi(argv[i+1]) );  // Flawfinder: ignore
                i++; continue;
            }

            if ( !strcmp(argv[i],"-b") || !strcmp(argv[i],"--bind"))
            {
                svr.set_ipv4_bind_address( argv[i+1] );
                i++; continue;
            }

            if ( !strcmp(argv[i],"-p") ||!strcmp(argv[i],"--port"))
            {
                svr.set_port( atoi(argv[i+1]) );        // Flawfinder: ignore
                i++; continue;
            }
        }


        //---------------------
        // 2-arg flags
        //---------------------
        if (i+2 < argc)
        {
            // --zone_a echo.example.com  10.0.0.5

            if ( !strcmp(argv[i],"-z") ||!strcmp(argv[i],"--zone_a"))
            {
                svr.set_zone_a( argv[i+1], argv[i+2] );
                i+=2; continue;
            }

            if ( !strcmp(argv[i],"-n") ||!strcmp(argv[i],"--ns_a"))
            {
                svr.add_ns_a( argv[i+1], argv[i+2] );
                i+=2; continue;   // consume arg
            }
        }

        // Catch bad arguments

        fprintf(stderr, "\nERROR: %s :\n"
        "  Unknown flag: %s\n", argv[0], argv[i]);

        emit_help(argv[0]);
    }


    // Enforce required args
    if ( ! strlen( svr.get_zone_fqdn() ))               // Flawfinder: ignore
    {
        fprintf(stderr,
                "\nERROR: %s:\n"
                "  You must specify the zone for which this nameserver\n"
                "  is authoritative (e.g.:  echo.example.com).\n"
                "  Use either the '--zone_a' or '-z' flag to do this.\n\n",
                argv[0]);
        emit_help(argv[0]);
    }

    // Enforce required args
    if ( ! svr.get_ns_host_count() )
    {
        fprintf(stderr,
                "\nERROR: %s:\n"
                "  You must specify at least 1 nameserver within this "
                "zone via the --ns_a (or -n) parameter.\n",
                argv[0]);
        emit_help(argv[0]);
    }

    if ( ! strlen( svr.get_bind_quad() ) )              // Flawfinder: ignore
    {
        fprintf(stderr,
                "\nERROR: %s:\n"
                "  Please specify an IP address for EchoDNS to listen on\n"
                "  (via --bind or -b).  This must IP address must correspond\n"
                "  to a network device on the machine hosting this instance\n"
                "  of EchoDNS.\n",
                argv[0]);
        emit_help(argv[0]);
    }

    svr.bind_UDP_socket();
    svr.process_requests();    // Start accepting requests from clients

    /* never reached */
    return 0;
}
