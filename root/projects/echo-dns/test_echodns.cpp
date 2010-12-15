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
// File    test_echodns.cpp
//
// Use     tests echodns by sending it a large number of lookup requests
//
// Usage Example:
//         test_echodns --query 127-0-0-1.localdomain.lan --ipaddr 192.168.1.99
//
// To build this program, type:
//      g++ -o test_echodns  test_echodns.cpp 
//
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


void emit_udp_packet( int             udp_sock, 
                      unsigned char * resp, 
                      int             size,
                      sockaddr      * client_addr,
                      socklen_t       sizeof_client_addr 
                    );

void emit_help(char *appname);

#define MAX_MESG_SIZE 512
static const char *Version = "1.0";
bool Debug_ = false;
bool Log_   = false;

//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------

int main(int argc, char *argv[])
{
    unsigned char mesg[MAX_MESG_SIZE];
    unsigned char resp[MAX_MESG_SIZE];

    char *effective_user = 0;            // run as this user after bind
    bool quiet_flag      = false;

    int   udp_sock             = 0, 
          server_port          = 53,
          status               = 0, 
          size                 = 0; 

    unsigned int ttl_in_network_order =  htonl(86400);   // 1 day


    int   qname_label_offset[256];  // offsets into UDP packet of QNAME lables
    char  domain_query[256];        // this nameserver's authoritative query
    char  interface[16];
    int   query_length;
    interface[0]    = 0;
    domain_query[0]     = 0;

    int query_labels = 1;           // example.com. == 3  because the traling 
                                    // dot is the empty label ""  
                                    // 
                                    //        "example"  == 1
                                    //        "com"      == 2
                                    //        ""         == 3


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

        if ( !strcmp(argv[i],"-q") ||!strcmp(argv[i],"--quiet"))
        {   
            quiet_flag = true;
            continue;
        }

        if ( !strcmp(argv[i],"-l") ||!strcmp(argv[i],"--log"))
        {   
            Log_ = true;
            continue;
        }


        if ( !strcmp(argv[i],"-d") ||!strcmp(argv[i],"--debug"))
        {   
            Debug_ = true;
            continue;
        }


        //---------------------
        // 1-arg flags
        //---------------------

        if (i+1 < argc)
        {
            if ( !strcmp(argv[i],"-q") ||!strcmp(argv[i],"--query"))
            {
                if ( (query_length = strlen(argv[i+1]) )  >= 
                     (sizeof(domain_query) -2 )
                   )
                {
                     fprintf(stderr,
                             "ERROR: %s : domain too long (%d >= %d)\n", 
                              argv[0], query_length, sizeof(domain_query)-2);
                    exit(1);
                }

                // Make sure the query ends in a '.'
                memcpy(domain_query, argv[i+1], query_length +1 );
                if ( domain_query[ query_length -1 ] != '.' )
                {
                    domain_query[ query_length ] = '.';
                    domain_query[ query_length + 1] = 0;
                    query_length ++;
                }

                i++; continue;   // consume arg
            }


            if ( !strcmp(argv[i],"-i") || !strcmp(argv[i],"--ipaddr"))
            {
                // TODO:  be stricter about dottedquad here
                //        Right now I'm just relying on the
                //        system error message if the user
                //        gets it wrong.

                int len =  strlen(argv[i+1]);

                if ( len >= sizeof( interface ) )
                {
                    fprintf(stderr, "ERROR:  %s <dottedquad> too long)\n",
                            argv[0]);
                    exit(1);
                }
                strncpy(interface, argv[i+1], len);
                interface[len] = 0;

                i++; continue;   // consume arg
            }

            if ( !strcmp(argv[i],"-p") ||!strcmp(argv[i],"--port"))
            {
                server_port = atoi(argv[i+1]);

                if ( server_port != 53 &&  ! quiet_flag )
                {
                    fprintf(stderr, 
                       "\nWARNING: %s :\n"
                       "   You have chosen a port number other than 53.\n"
                       "   However, there are valid reasons to choose a\n"
                       "   non-standard port if you're using port-forwarding,\n"
                       "   but be sure you know what you're doing.\n\n"
                       "   For a list of reserved ports see:\n"
                       "   http://www.iana.org/assignments/port-numbers\n\n"
                       "   To suppress this warning, use the '--quiet' flag\n"
                       "   prior to specifying a non-standard port\n\n",
                       argv[0]
                    );
                }
                i++; continue;   // consume arg
            }

            if ( !strcmp(argv[i],"-t") ||!strcmp(argv[i],"--ttl"))
            { 
                ttl_in_network_order = htonl( atoi(argv[i+1] ) );
                i++; continue;   // consume arg
            }

            if ( !strcmp(argv[i],"-u") ||!strcmp(argv[i],"--user"))
            { 
                effective_user = argv[i+1];
                i++; continue;   // consume arg
            }
        }     

        // Catch bad arguments

        fprintf(stderr, "\nERROR: %s :\n"
        "  Unknown flag: %s\n", argv[0], argv[i]);

        emit_help(argv[0]);     
    }


    // Enforce required args
    if ( ! domain_query[0] ) 
    { 
        fprintf(stderr,
                "\nERROR: %s:\n"
                "  You must specify the query.\n"
                "  Use either the '--query' or '-q' flag to do this.\n\n",
                argv[0]);
        emit_help(argv[0]); 
    }


    sockaddr_in server_addr, client_addr;
    socklen_t   sizeof_client_addr = sizeof(client_addr);
    memset(&server_addr, 0, sizeof(server_addr));


    // Create UDP socket

    if ( (udp_sock = socket( AF_INET, SOCK_DGRAM, IPPROTO_UDP ) ) == -1 )
    { 
        perror("socket()");  
        exit(1); 
    }

    server_addr.sin_family      = AF_INET;                    // Internet addr
    server_addr.sin_port        = htons(server_port);         // server port
    server_addr.sin_addr.s_addr =  ( interface[0] )           // specific addr?
                                   ?  inet_addr( interface )  // then use it
                                   :  htonl(INADDR_ANY);      // else use all


    //-----------------------------------
    // Header section
    //-----------------------------------

    // copy ID of mesg to resp
    mesg[0] = 0; 
    mesg[1] = 1;

    mesg[2] = 1;     // recursion desired
    mesg[3] = 0;    

    // QDCOUNT
    mesg[4] =  0;
    mesg[5] =  1;

    // ANCOUNT
    mesg[6] =  0;
    mesg[7] =  0;

    // NSCOUNT      - no authority RRs
    mesg[8] =  0;
    mesg[9] =  0;

    // ARCOUNT      - no additional RRs
    mesg[10] =  0;
    mesg[11] =  0;


    //-----------------------------------
    // Question section
    //      Note:  question is reiterated
    //             in the response.
    //-----------------------------------

    // QNAME
    int eoq = 12;

    char *head = domain_query;
    char *tail = head;
    while (tail =  strchr(head,'.') )
    {
        *tail = 0;
        int label_length = tail - head;

        printf("segment: %d %s\n", label_length, head );

        mesg[ eoq ] = label_length;
        eoq++;
        memcpy(mesg + eoq, head, label_length );
        eoq += label_length;
        tail ++;
        head = tail;
    }
    mesg[ eoq ] = 0;

    // QTYPE
    mesg[eoq + 1] = 0;
    mesg[eoq + 2] = 1;


    // QCLASS
    mesg[eoq + 3] = 0;
    mesg[eoq + 4] = 1;

    emit_udp_packet( udp_sock,
                     mesg,
                     eoq + 5,
                     (sockaddr *) &server_addr,
                     sizeof_client_addr 
                   );
}



//-----------------------------------------------------------------------------
// emit_udp_packet --
//       Writes a UDP packet to the cient
//-----------------------------------------------------------------------------
void emit_udp_packet( int             udp_sock, 
                      unsigned char * resp, 
                      int             size,
                      sockaddr      * client_addr,
                      socklen_t       sizeof_client_addr 
                    )
{

    if ( Debug_ )
    {
        fprintf(stdout, "sendto args:\n" 
               "   udp_sock: %d\n" 
               "   resp: \n", udp_sock);
        
        for (int i=0; i< size; i++)
        {
            fprintf(stdout," %d", (int) resp[i]);
        }
        fprintf(stdout,"\n");
        fflush(stdout);
    }


    for (int xx=0; xx<3; xx++)
    {
        int status = sendto( udp_sock, 
                             resp, 
                             size, 
                             0,
                             client_addr, 
                             sizeof_client_addr
                           );

        if (status != size) 
        {
            perror("socket()");  
            fprintf(stderr, "sendto(): short write (%d).\n", status); 
            // exit(1); 
        }
    }
}

//-----------------------------------------------------------------------------
// emit_help --
//      Emit help message and exit with a non-zero status code. 
//-----------------------------------------------------------------------------
void emit_help(char *appname)
{
            printf("\n"
 "Syntax:  %s \\ \n"
 "    [ {-h | --help   }              ]    (print this help message)\n"
 "    [ {-v | --version}              ]    (print version string)\n"
 "    [ {-q | --quiet  }              ]    (no warnings at startup)\n"
 "    [ {-l | --log    }              ]    (log src/dest IP to stdout)\n"
 "    [ {-d | --debug  }              ]    (emit debug messages)\n"
 "      {-q | --query  } <query>           (moo.127-0-0-1.ipaddr.localdomain.lan)\n"
 "    [ {-i | --ipaddr } <dottedquad> ]    (IP of destination nameserver)\n"
 "    [ {-p | --port   } <portnum>    ]    (default: 53)\n"
 "    [ {-t | --ttl    } <seconds>    ]    (DNS data time to live "
                                           "default: 86400)\n"
 "    [ {-u | --user   } <username>   ]    (run as after binding to port)\n\n"
 "Example:\n"
 "    %s --query ipaddr.localdomain.lan --ipaddr 192.168.1.99\n"
 "\n",
                appname,appname);
            exit(1);
}
