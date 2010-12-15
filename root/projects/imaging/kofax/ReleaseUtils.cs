/// Author:    Manfung Chan
/// Version:   v1.0

using System;
using System.Collections.Generic;
using System.Text;

namespace KofaxAlfrescoRelease_v1
{
    using AscentRelease;

    class ReleaseUtils
    {

        private ReleaseSetupData releaseSetUpData;
        
        public ReleaseUtils(ReleaseSetupData releaseSetUpData)
        {
            this.releaseSetUpData = releaseSetUpData;
        }

        //**************************************************************************
        // Property (Get/Set):		SetupData
        // Purpose:					This will get the release setup data object.
        //**************************************************************************
        internal ReleaseSetupData SetupData
        {
            get
            {
                return this.releaseSetUpData;
            }
        }

        internal static AscentRelease.Link getLink(AscentRelease.Links arLinks, String linkName) 
        {
            try
            {
                object oLinkToGet = (object)linkName;
                return arLinks.get_Item(ref oLinkToGet);
            }
            catch (Exception e)
            {
                // if the link does not exist just return null
                return null;
            }
        }

        internal static String getLinkDestination(AscentRelease.Links arLinks, String linkName)
        {
            try
            {
                foreach ( AscentRelease.Link link in arLinks ) {
                    if (link.Source.Equals(linkName)) {
                        return link.Destination;
                    }                    
                }

                // no need to check for null as the get_item throws exception if not found
                return null;
            }
            catch (Exception e)
            {
                // if the link does not exist just return null
                return null;
            }
        }

        internal static String getLinkValue(AscentRelease.Values arValues, String linkName)
        {
            try
            {
                foreach (AscentRelease.Value value in arValues)
                {
                    if (value.Destination.Equals(linkName))
                    {
                        return value.Value;
                    }
                }

                // no need to check for null as the get_item throws exception if not found
                return null;
            }
            catch (Exception e)
            {
                // if the link does not exist just return null
                return null;
            }
        }

        //*********************************************************
        // Function:	getCustomProperty()
        // Scope:		internal
        // Overview:	This is a static function that returns the 
        //				value for a specific custom property whose 
        //				name is passed in to search for.
        // Params:		properties- The custom properties to write to.
        //				name-	    Name of the CustomProperty to retreive.
        // Returns:		string-		The CustomProperty value.
        internal static String getCustomProperty(AscentRelease.CustomProperties properties, String name)
        {
            try
            {
                object oPropToGet = (object)name;
                AscentRelease.CustomProperty oProperty = properties.get_Item(ref oPropToGet);
                // no need to check for null as the get_item throws exception if not found
                return oProperty.Value;
            }
            catch (Exception e)
            {
                // if the link does not exist just return null
                return null;
            }
        }

        //***********************************************************
        //
        // Method:		OnIndexFieldDelete
        // Purpose:		An index field was deleted from the existing document class
        // Params:		strIndexField-		Index field being deleted
        // Output:		None
        //
        //************************************************************/
        internal static bool OnIndexFieldDelete(string strIndexField, ReleaseSetupData oSetupData)
        {
            bool bShowUI = false;

            try
            {                

                String m_strDeletedIndexField = strIndexField;
                KfxLinkSourceType m_kfxlnkDeletedFieldType = KfxLinkSourceType.KFX_REL_INDEXFIELD;

                //locate all sourcetypes in the links collection which are IndexField related
                //and remove them but make sure that we add the destination back in to the collection

                foreach (Link oLink in oSetupData.Links)
                {
                    if ((oLink.Source.EndsWith(strIndexField)) &&
                        (oLink.SourceType == m_kfxlnkDeletedFieldType))
                    {
                        oLink.Source = "";
                        oLink.SourceType = KfxLinkSourceType.KFX_REL_UNDEFINED_LINK;
                        bShowUI = true;
                    }
                }

                //FIX SPR#27062
                GC.Collect();
                GC.WaitForPendingFinalizers();

                oSetupData.Apply();
                return bShowUI;
            }
            catch (Exception e)
            {

                return bShowUI;
            }
        }

        //***********************************************************
        //
        // Method:		OnIndexFieldRename
        // Purpose:		An index field in the document class was renamed
        // Params:		strOldName-			Index field being renamed
        //				strNewName-			New name for index field
        // Output:		None
        //
        //************************************************************/
        internal static void OnIndexFieldRename(string strOldName, string strNewName,
            ReleaseSetupData oSetupData)
        {
            try
            {

                //locate all sourcetypes in the links collection which are BatchField related 
                //and remove them but make sure that we add the destination back in to the collection

                foreach (Link oLink in oSetupData.Links)
                {
                    if ((oLink.Source.EndsWith(strOldName)) &&
                        (oLink.SourceType == KfxLinkSourceType.KFX_REL_INDEXFIELD))
                    {
                        oLink.Source = strNewName;
                    }
                }

                //FIX SPR#27062
                GC.Collect();
                GC.WaitForPendingFinalizers();

                oSetupData.Apply();
            }
            catch (Exception e)
            {

            }            
        }

        /*
    SQL_UNKNOWN_TYPE  	0  	(Unknown data type)
    SQL_CHAR 	        1 	A fixed-length character string
    SQL_NUMERIC     	2 	A floating-point value (double precision)
    SQL_DECIMAL     	3 	A floating-point value (greater than double precision)
    SQL_INTEGER     	4 	A long integer.
    SQL_SMALLINT    	5 	A short integer.
    SQL_FLOAT 	        6 	A floating-point value (double precision)
    SQL_REAL 	        7 	A floating-point value (single precision)
    SQL_DOUBLE      	8 	A floating-point value (double precision)
    SQL_DATETIME    	9 	A date.
    SQL_VARCHAR     	12	Variable-length character string.
 */
        internal static bool equalsType(AscentRelease.KfxIndexFieldType kfxtype, String alfrescoType)
        {
            switch (kfxtype)
            {
                case AscentRelease.KfxIndexFieldType.SQL_UNKNOWN_TYPE:
                    break;
                case AscentRelease.KfxIndexFieldType.SQL_CHAR:
                    if (alfrescoType.EndsWith("text"))
                    {
                        
                        return true;
                    }
                    break;
                case AscentRelease.KfxIndexFieldType.SQL_NUMERIC:
                    if (alfrescoType.EndsWith("float"))
                    {
                        return true;
                    }
                    break;
                case AscentRelease.KfxIndexFieldType.SQL_DECIMAL:
                    if (alfrescoType.EndsWith("float"))
                    {
                        return true;
                    }
                    break;
                case AscentRelease.KfxIndexFieldType.SQL_INTEGER:
                    if (alfrescoType.EndsWith("int"))
                    {
                        return true;
                    }
                    break;
                case AscentRelease.KfxIndexFieldType.SQL_SMALLINT:
                    if (alfrescoType.EndsWith("float"))
                    {
                        return true;
                    }
                    break;
                case AscentRelease.KfxIndexFieldType.SQL_FLOAT:
                    if (alfrescoType.EndsWith("float"))
                    {
                        return true;
                    }
                    break;
                case AscentRelease.KfxIndexFieldType.SQL_REAL:
                    if (alfrescoType.EndsWith("float"))
                    {
                        return true;
                    }
                    break;
                case AscentRelease.KfxIndexFieldType.SQL_DOUBLE:
                    if (alfrescoType.EndsWith("double"))
                    {
                        return true;
                    }
                    break;
                case AscentRelease.KfxIndexFieldType.SQL_DATETIME:
                    if (alfrescoType.EndsWith("date") || alfrescoType.EndsWith("datetime"))
                    {
                        return true;
                    }
                    break;
                case AscentRelease.KfxIndexFieldType.SQL_VARCHAR:
                    if (alfrescoType.EndsWith("text"))
                    {
                        return true;
                    }
                    break;
            }
            return false;
        }


        internal static string[] SplitByString(string source, string split)
        {
            int offset = 0;
            int index = 0;
            int[] offsets = new int[source.Length + 1];

            while (index < source.Length)
            {
                int indexOf = source.IndexOf(split, index);
                if (indexOf != -1)
                {
                    offsets[offset++] = indexOf;
                    index = (indexOf + split.Length);
                }
                else
                {
                    index = source.Length;
                }
            }

            string[] final = new string[offset + 1];
            if (offset == 0)
            {
                final[0] = source;
            }
            else
            {
                offset--;
                final[0] = source.Substring(0, offsets[0]);
                for (int i = 0; i < offset; i++)
                {
                    final[i + 1] = source.Substring(offsets[i] + split.Length, offsets[i + 1] - offsets[i] - split.Length);
                }
                final[offset + 1] = source.Substring(offsets[offset] + split.Length);
            }
            return final;
        }

    }
}
