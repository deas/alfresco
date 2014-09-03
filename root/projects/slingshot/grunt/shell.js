/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

'use strict';

/**
 * Grunt tasks that use the shell plugin to run commands in a unix shell.
 */

module.exports = function (grunt, alf) {

   var _ = require('lodash'), // Add the lodash util library
      extend = _.extend;

   // Shell Commands run by grunt
   // @see: https://github.com/sindresorhus/grunt-shell
   // Community Config to support the community who don't have access to our Dev Env.
   // Alfresco Config makes use of Dev Env commands that allow us to build Enterprise as well

   var communityConfig = {
         // Reset Share's Caches:
         resetCaches: {
            command: 'curl -s -d "reset=on" --header "Accept-Charset:ISO-8859-1,utf-8" --header "Accept-Language:en" -u admin:admin http://localhost:8081/share/service/index ;  curl -s "http://localhost:8081/share/page/caches/dependency/clear" -H "Content-Type: application/x-www-form-urlencoded" --data "submit=Clear+Dependency+Caches" -u admin:admin',
            options: {
               stdout: true,
               stderr: true,
               failOnError: true
            }
         },

         // start share & alfresco
         // Assumes script called "start-tomcat" and "start-app-tomcat" exist.
         startRepo: {
            command: 'mvn install -pl projects/web-client,projects/solr -Psolr-http -DskipTests && start-tomcat',
            options: {
               stdout: true,
               stderr: true,
               failOnError: true
            }
         },
         startRepoExistingBuild: {
            command: 'start-tomcat',
            options: {
               stdout: true,
               stderr: true,
               failOnError: true
            }
         },
         se: {
            command: 'mvn prepare-package -pl projects/slingshot -DskipTests -Dmaven.yuicompressor.skip',
            options: {
               stdout: true,
               stderr: true,
               failOnError: true
            }
         },
         startShare: {
            command: 'mvn prepare-package -pl projects/slingshot -DskipTests -Dmaven.yuicompressor.skip && start-app-tomcat',
            options: {
               stdout: true,
               stderr: true,
               failOnError: true
            }
         },
         startShareInc: {
            command: 'mvn install -pl projects/slingshot -DskipTests && start-app-tomcat',
            options: {
               stdout: true,
               stderr: true,
               failOnError: true
            }
         }
      },
      alfrescoConfig = {
         // Reset Share's Caches:
         resetCaches: {
            command: 'ws -s; ds -s',
            options: {
               stdout: true,
               stderr: true,
               failOnError: true
            }
         },

         // start share & alfresco
         startRepo: {
            command: 'm r -ie && m r -t',
            options: {
               stdout: true,
               stderr: true,
               failOnError: true
            }
         },
         startRepoExistingBuild: {
            command: 'm r -t',
            options: {
               stdout: true,
               stderr: true,
               failOnError: true
            }
         },
         se: {
            command: 'm s -e',
            options: {
               stdout: true,
               stderr: true,
               failOnError: true
            }
         },
         startShare: {
            command: 'm s -e && m s -t',
            options: {
               stdout: true,
               stderr: true,
               failOnError: true
            }
         },
         startShareInc: {
            command: 'm s -ie && m s -t',
            options: {
               stdout: true,
               stderr: true,
               failOnError: true
            }
         }
      },
      sharedConfig = {
         antClean: {
            command: 'ant clean',
            options: {
               stdout: true,
               stderr: true,
               failOnError: true,
               execOptions: {
                  cwd: alf.rootDir
               }
            }
         },
         mvnClean: {
            command: 'mvn clean',
            options: {
               stdout: true,
               stderr: true,
               failOnError: true,
               execOptions: {
                  cwd: alf.rootDir
               }
            }
         },
         // Generate JSDocs
         jsdoc: {
            command: 'jsdoc ../../' + alf.jsdocFiles + ' -c ../../conf.json', // TODO: Make this work with defined paths.
            options: {
               stdout: true,
               stderr: true,
               failOnError: true,
               execOptions: {
                  cwd: alf.nodeBinDir
               }
            }
         },

         jsdocServer: {
            command: 'python -m SimpleHTTPServer 8082',
            options: {
               stdout: true,
               stderr: true,
               failOnError: true,
               execOptions: {
                  cwd: alf.docsDir
               }
            }
         },

         // selenium
         seleniumUp: {
            command: 'java -jar selenium*.jar',
            options: {
               stdout: true,
               stderr: true,
               failOnError: true,
               execOptions: {
                  cwd: alf.testResourcesDir + "/selenium",
                  maxBuffer: "Infinite"
               }
            }
         },

         // Stop running servers (I don't know of a more friendly but equally effective than this).
         killRepo: {
            command: 'kill `lsof -t -i :8080 -sTCP:LISTEN`',
            options: {
               stdout: true,
               stderr: true,
               failOnError: false
            }
         },
         killShare: {
            command: 'kill `lsof -t -i :8081 -sTCP:LISTEN`',
            options: {
               stdout: true,
               stderr: true,
               failOnError: false
            }
         },

         svnUp: {
            command: 'pwd;svn up',
            options: {
               stdout: true,
               stderr: true,
               failOnError: true,
               execOptions: {
                  cwd: alf.codeDir
               }
            }
         },

         // See also vagrant.js
         // Start the vagrant VM
         vagrantUp: {
            command: 'vagrant up',
            options: {
               stdout: true,
               stderr: true,
               failOnError: true,
               execOptions: {
                  cwd: alf.testResourcesDir
               }
            }
         },
         // Reset the vagrant VM
         vagrantDestroy: {
            command: 'vagrant destroy -f',
            options: {
               stdout: true,
               stderr: true,
               failOnError: true,
               execOptions: {
                  cwd: alf.testResourcesDir
               }
            }
         },

         // Set up an already running Vagrant VM instance
         vagrantProvision: {
            command: 'vagrant provision',
            options: {
               stdout: true,
               stderr: true,
               failOnError: true,
               execOptions: {
                  cwd: alf.testResourcesDir
               }
            }
         },

         // Shutdown a running vagrant VM istance
         vagrantHalt: {
            command: 'vagrant halt',
            options: {
               stdout: true,
               stderr: true,
               failOnError: true,
               execOptions: {
                  cwd: alf.testResourcesDir
               }
            }
         }
      },
      configToMerge = (process.env.CURRENT_PROJECT)? alfrescoConfig : communityConfig,
      shellConfig = extend(sharedConfig, configToMerge);

   // Return the config. This gets pushed into the grunt.init.config method in Gruntfile.
   return {
      shell: shellConfig
   };
};