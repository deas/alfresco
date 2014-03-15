#!/bin/sh
set -e

if [ -e /.installed ]; then
   echo 'Already installed.'

else
   echo ''
   echo 'INSTALLING'
   echo '----------'

   # Add Google public key to apt
   wget -q -O - "https://dl-ssl.google.com/linux/linux_signing_key.pub" | sudo apt-key add -

   # Add Google to the apt-get source list
   echo 'deb http://dl.google.com/linux/chrome/deb/ stable main' >> /etc/apt/sources.list

   # Update app-get
   apt-get update

   # Install Java, Chrome, Xvfb, unzip, firefox and libicu48
   apt-get -y install openjdk-7-jre google-chrome-stable xvfb unzip firefox libicu48

   cd /tmp

   # Download and copy the ChromeDriver to /usr/local/bin
   # wget "https://chromedriver.googlecode.com/files/chromedriver_linux64_2.3.zip"
   # unzip chromedriver_linux64_2.3.zip
   wget "http://chromedriver.storage.googleapis.com/2.9/chromedriver_linux64.zip"
   unzip chromedriver_linux64.zip
   mv chromedriver /usr/local/bin

   # Download and copy Phantomjs to /usr/local/bin
   # wget "http://phantomjs.googlecode.com/files/phantomjs-1.9.2-linux-x86_64.tar.bz2"
   # tar -xjvf phantomjs-1.9.2-linux-x86_64.tar.bz2
   # mv phantomjs-1.9.2-linux-x86_64/bin/phantomjs /usr/local/bin
   wget "https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-1.9.7-linux-x86_64.tar.bz2"
   tar -xjvf phantomjs-1.9.7-linux-x86_64.tar.bz2
   mv phantomjs-1.9.7-linux-x86_64/bin/phantomjs /usr/local/bin

   # Download and copy Selenium to /usr/local/bin
   wget "https://selenium.googlecode.com/files/selenium-server-standalone-2.39.0.jar"
   mv selenium-server-standalone-2.39.0.jar /usr/local/bin

   # So that running `vagrant provision` doesn't redownload everything
   touch /.installed
fi

# Start Xvfb, Chrome, and Selenium in the background
export DISPLAY=:10
cd /vagrant

echo "Starting Xvfb ..."
Xvfb :10 -screen 0 1366x768x24 -ac &

echo "Starting Google Chrome ..."
google-chrome --remote-debugging-port=9222 &

echo "Starting Firefox ..."
firefox &

echo "Starting Phantomjs ..."
phantomjs --ignore-ssl-errors=true --web-security=false --webdriver=192.168.56.4:4444 &

echo "Starting Selenium ..."
cd /usr/local/bin
nohup java -jar ./selenium-server-standalone-2.39.0.jar &