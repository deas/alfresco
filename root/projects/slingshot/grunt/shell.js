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
   // Return the config. This gets pushed into the grunt.init.config method in Gruntfile.
   return {
      // Shell Commands run by grunt
      // @see: https://github.com/sindresorhus/grunt-shell
      shell: {

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

         // start share & alfresco
         startRepo: {
            command: 'r -ie && r -t',
            options: {
               stdout: true,
               stderr: true,
               failOnError: true
            }
         },
         startShare: {
            command: 's -e && s -t',
            options: {
               stdout: true,
               stderr: true,
               failOnError: true
            }
         },
         startShareInc: {
            command: 's -ie && s -t',
            options: {
               stdout: true,
               stderr: true,
               failOnError: true
            }
         },
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
      }
   }
}