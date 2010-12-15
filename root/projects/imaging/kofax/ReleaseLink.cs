/// Author:    Manfung Chan
/// Version:   v1.0

using System;
using System.Collections.Generic;
using System.Text;
using System.Collections;

namespace KofaxAlfrescoRelease_v1
{
    class ReleaseLink
    {

        private String indexName;
        private String destinationName;
        private String displayDestinationName;
        private String type;
        private ArrayList indexOptions;
        private ArrayList removedIndexes = new ArrayList();

        private bool mandatory = false;

        public String IndexFieldName
        {
            get
            {
                return this.indexName;
            }
            set
            {
                this.indexName = value;
            }
        }

        public String DisplayDestinationName
        {
            get { return this.displayDestinationName; }
            set { this.displayDestinationName = value; }
        }

        public String Destination
        {
            get
            {
                return this.destinationName;
            }
            set
            {
                this.destinationName = value;
            }
        }

        public String Type
        {
            get
            {
                return this.type;
            }
            set
            {
                this.type = value;
            }
        }

        internal ArrayList IndexOptions
        {
            get { return indexOptions; }
            set { 

                indexOptions = value;
                indexOptions.Sort();
            }
        }

        public ArrayList RemovedIndexes
        {
            get { return removedIndexes; }
            set { removedIndexes = value; }
        }

        public bool Mandatory
        {
            get { return mandatory; }
            set { mandatory = value; }
        }

        public ReleaseLink(String displayDestinationName, String destinationName, String type)
        {
            this.displayDestinationName = displayDestinationName;
            this.destinationName = destinationName;
            this.type = type;
        }

    }
}
