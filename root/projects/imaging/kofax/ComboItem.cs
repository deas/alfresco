using System;
using System.Collections.Generic;
using System.Text;

namespace KofaxAlfrescoRelease_v1
{
    class ComboItem
    {
        protected string text;
        protected string itemData;
        protected ReleaseLink[] releaselinks;
        protected Object itemTag;

        internal ReleaseLink[] Releaselinks
        {
            get { return releaselinks; }
            set { releaselinks = value; }
        }

        public ComboItem(string Text, string ItemData)
        {
            text = Text;
            itemData = ItemData;
        }

        public string ItemData
        {
            get
            {
                return itemData;
            }
            set
            {
                itemData = value;
            }
        }

        public override string ToString()
        {
            return text;
        }

        public Object ItemTag
        {
            get { return itemTag; }
            set { itemTag = value; }
        }

    }
}
