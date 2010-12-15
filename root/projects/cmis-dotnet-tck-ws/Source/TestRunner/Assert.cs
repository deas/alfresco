/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
using System;
using System.Net;
using System.Net.Security;
using System.ServiceModel;
using System.Collections.Generic;
using System.Security.Cryptography.X509Certificates;
using System.Configuration;
using System.Reflection;
using WcfCmisWSTests.CmisServices;

///
/// author: Stas Sokolovsky
///
namespace WcfCmisWSTests
{
    public class Assert
    {
        public static void IsNotNull(object obj, string failMessage)
        {
            if (obj == null)
            {
                throw new AssertionException(failMessage);
            }
        }

        public static void IsTrue(bool value, string failMessage)
        {
            if ((null == value) || !value)
            {
                throw new AssertionException(failMessage);
            }
        }

        public static void IsFalse(bool value, string failMessage)
        {
            IsTrue(!value, failMessage);
        }

        public static void Fail(string failMessage)
        {
            throw new AssertionException(failMessage);
        }

        public static void Skip(string skippingMessage)
        {
            throw new SkippedException(skippingMessage);
        }

        public static void AreEqual(object obj1, object obj2, string failMessage)
        {
            if (null == obj1)
            {
                if (null != obj2)
                {
                    throw new AssertionException(failMessage);
                }
                return;
            }
            if (null == obj2)
            {
                throw new AssertionException(failMessage);
            }
            if (!obj1.GetType().Equals(obj2.GetType()))
            {
                throw new AssertionException("Expected object type: " + obj1.GetType().ToString() + ", but actual object type is: " + obj2.GetType().ToString());
            }
            if (obj1 is Array)
            {
                AreEqual((Array)obj1, (Array)obj2, failMessage);
                return;
            }
            if (obj1 == obj2)
            {
                return;
            }

            if (!obj1.Equals(obj2))
            {
                throw new AssertionException(failMessage);
            }
        }

        public static void AreNotEqual(object obj1, object obj2, string failMessage)
        {
            if ((null == obj1) && (null == obj2))
            {
                throw new AssertionException(failMessage);
            }
            if (null == obj2)
            {
                return;
            }

            if (!obj1.GetType().Equals(obj2.GetType()))
            {
                return;
            }

            if (obj1.Equals(obj2))
            {
                throw new AssertionException(failMessage);
            }
        }

        public static void AreEqual(Array obj1, Array obj2, string failMessage)
        {
            if (obj1 == obj2)
            {
                return;
            }
            if (null == obj1)
            {
                if (null != obj2)
                {
                    throw new AssertionException(failMessage);
                }
                return;
            }
            if (null == obj2)
            {
                throw new AssertionException(failMessage);
            }
            if (!obj1.GetType().Equals(obj2.GetType()))
            {
                throw new AssertionException(failMessage + ". Expected object type: " + obj1.GetType().ToString() + ", but actual object type is: " + obj2.GetType().ToString());
            }
            if (!obj1.Length.Equals(obj2.Length))
            {
                throw new AssertionException(failMessage);
            }

            System.Collections.IEnumerator first = obj1.GetEnumerator();
            System.Collections.IEnumerator second = obj2.GetEnumerator();

            while (first.MoveNext() && second.MoveNext())
            {
                if (((null != first.Current) && !first.Current.Equals(second.Current)) || ((null == first.Current) && (null != second.Current)))
                {
                    throw new AssertionException(failMessage);
                }
            }
        }

        public class AssertionException : Exception
        {
            public AssertionException() : base() { }
            public AssertionException(string message) : base(message) { }
            public AssertionException(string message, System.Exception inner) : base(message, inner) { }
        }

    }

    public class SkippedException : Exception
    {
        public SkippedException() : base() { }
        public SkippedException(string message) : base(message) { }
        public SkippedException(string message, System.Exception inner) : base(message, inner) { }
    }
}
