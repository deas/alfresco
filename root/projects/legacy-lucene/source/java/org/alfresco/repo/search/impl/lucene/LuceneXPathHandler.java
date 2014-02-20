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
package org.alfresco.repo.search.impl.lucene;

import java.util.ArrayList;

import org.alfresco.repo.search.impl.lucene.analysis.PathTokenFilter;
import org.alfresco.repo.search.impl.lucene.query.AbsoluteStructuredFieldPosition;
import org.alfresco.repo.search.impl.lucene.query.DescendantAndSelfStructuredFieldPosition;
import org.alfresco.repo.search.impl.lucene.query.PathQuery;
import org.alfresco.repo.search.impl.lucene.query.RelativeStructuredFieldPosition;
import org.alfresco.repo.search.impl.lucene.query.SelfAxisStructuredFieldPosition;
import org.alfresco.repo.search.impl.lucene.query.StructuredFieldPosition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO9075;
import org.jaxen.saxpath.Axis;
import org.jaxen.saxpath.Operator;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.saxpath.XPathHandler;

/**
 * XPATH handler for lucene - currently only does basic paths
 * 
 * @author andyh
 *
 */
public class LuceneXPathHandler implements XPathHandler
{
    private PathQuery query;

    private boolean isAbsolutePath = true;

    int absolutePosition = 0;

    private NamespacePrefixResolver namespacePrefixResolver;

    private DictionaryService dictionaryService;

    /**
     * 
     */
    public LuceneXPathHandler()
    {
        super();
    }

    /**
     * Get the resulting query
     * @return - the query
     */
    public PathQuery getQuery()
    {
        return this.query;
    }

    public void endAbsoluteLocationPath() throws SAXPathException
    {
        // No action
    }

    public void endAdditiveExpr(int op) throws SAXPathException
    {
        switch (op)
        {
        case Operator.NO_OP:
            break;
        case Operator.ADD:
        case Operator.SUBTRACT:
            throw new UnsupportedOperationException();
        default:
            throw new UnsupportedOperationException("Unknown operation " + op);
        }
    }

    public void endAllNodeStep() throws SAXPathException
    {
        // Nothing to do
        // Todo: Predicates
    }

    public void endAndExpr(boolean create) throws SAXPathException
    {
        if (create)
        {
            throw new UnsupportedOperationException();
        }
    }

    public void endCommentNodeStep() throws SAXPathException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void endEqualityExpr(int op) throws SAXPathException
    {
        switch (op)
        {
        case Operator.NO_OP:
            break;
        case Operator.EQUALS:
        case Operator.NOT_EQUALS:
            throw new UnsupportedOperationException();
        default:
            throw new UnsupportedOperationException("Unknown operation " + op);
        }
    }

    public void endFilterExpr() throws SAXPathException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void endFunction() throws SAXPathException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void endMultiplicativeExpr(int op) throws SAXPathException
    {
        switch (op)
        {
        case Operator.NO_OP:
            break;
        case Operator.MULTIPLY:
        case Operator.DIV:
        case Operator.MOD:
            throw new UnsupportedOperationException();
        default:
            throw new UnsupportedOperationException("Unknown operation " + op);
        }
    }

    public void endNameStep() throws SAXPathException
    {
        // Do nothing at the moment
        // Could have repdicates
    }

    public void endOrExpr(boolean create) throws SAXPathException
    {
        if (create)
        {
            throw new UnsupportedOperationException();
        }
    }

    public void endPathExpr() throws SAXPathException
    {
        // Already built
    }

    public void endPredicate() throws SAXPathException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void endProcessingInstructionNodeStep() throws SAXPathException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void endRelationalExpr(int op) throws SAXPathException
    {
        switch (op)
        {
        case Operator.NO_OP:
            break;
        case Operator.GREATER_THAN:
        case Operator.GREATER_THAN_EQUALS:
        case Operator.LESS_THAN:
        case Operator.LESS_THAN_EQUALS:
            throw new UnsupportedOperationException();
        default:
            throw new UnsupportedOperationException("Unknown operation " + op);
        }
    }

    public void endRelativeLocationPath() throws SAXPathException
    {
        // No action
    }

    public void endTextNodeStep() throws SAXPathException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void endUnaryExpr(int op) throws SAXPathException
    {
        switch (op)
        {
        case Operator.NO_OP:
            break;
        case Operator.NEGATIVE:
            throw new UnsupportedOperationException();
        default:
            throw new UnsupportedOperationException("Unknown operation " + op);
        }
    }

    public void endUnionExpr(boolean create) throws SAXPathException
    {
        if (create)
        {
            throw new UnsupportedOperationException();
        }
    }

    public void endXPath() throws SAXPathException
    {
        // Do nothing at the moment
    }

    public void literal(String arg0) throws SAXPathException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void number(double arg0) throws SAXPathException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void number(int arg0) throws SAXPathException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void startAbsoluteLocationPath() throws SAXPathException
    {
        if (!isAbsolutePath)
        {
            throw new IllegalStateException();
        }

    }

    public void startAdditiveExpr() throws SAXPathException
    {
        // Do nothing at the moment
    }

    public void startAllNodeStep(int axis) throws SAXPathException
    {
        switch (axis)
        {
        case Axis.CHILD:
            if (isAbsolutePath)
            {
                // addAbsolute(null, null);
                // We can always do relative stuff
                addRelative(null, null);
            }
            else
            {
                addRelative(null, null);
            }
            break;
        case Axis.DESCENDANT_OR_SELF:
            query.appendQuery(getArrayList(new DescendantAndSelfStructuredFieldPosition(), new DescendantAndSelfStructuredFieldPosition()));
            break;
        case Axis.SELF:
            query.appendQuery(getArrayList(new SelfAxisStructuredFieldPosition(), new SelfAxisStructuredFieldPosition()));
            break;
        default:
            throw new UnsupportedOperationException();
        }
    }

    private ArrayList<StructuredFieldPosition> getArrayList(StructuredFieldPosition one, StructuredFieldPosition two)
    {
        ArrayList<StructuredFieldPosition> answer = new ArrayList<StructuredFieldPosition>(2);
        answer.add(one);
        answer.add(two);
        return answer;
    }

    public void startAndExpr() throws SAXPathException
    {
        // Do nothing
    }

    public void startCommentNodeStep(int arg0) throws SAXPathException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void startEqualityExpr() throws SAXPathException
    {
        // Do nothing
    }

    public void startFilterExpr() throws SAXPathException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void startFunction(String arg0, String arg1) throws SAXPathException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void startMultiplicativeExpr() throws SAXPathException
    {
        // Do nothing at the moment
    }

    public void startNameStep(int axis, String nameSpace, String localName) throws SAXPathException
    {
        switch (axis)
        {
        case Axis.CHILD:
            if (isAbsolutePath)
            {
                // addAbsolute(nameSpace, localName);
                // we can always do relative stuff
                addRelative(nameSpace, localName);
            }
            else
            {
                addRelative(nameSpace, localName);
            }
            break;
        default:
            throw new UnsupportedOperationException();
        }

    }

    @SuppressWarnings("unused")
    private void addAbsolute(String nameSpace, String localName)
    {
        ArrayList<StructuredFieldPosition> answer = new ArrayList<StructuredFieldPosition>(2);
        // TODO: Resolve name space
        absolutePosition++;
        if ((nameSpace == null) || (nameSpace.length() == 0))
        {

            if(localName.equals("*"))
            {
                answer.add(new AbsoluteStructuredFieldPosition("*", absolutePosition));
            }
            else if (namespacePrefixResolver.getNamespaceURI("") == null)
            {
                answer.add(new AbsoluteStructuredFieldPosition(PathTokenFilter.NO_NS_TOKEN_TEXT, absolutePosition));
            }
            else
            {
                answer.add(new AbsoluteStructuredFieldPosition(namespacePrefixResolver.getNamespaceURI(""), absolutePosition));
            }

        }
        else
        {
            answer.add(new AbsoluteStructuredFieldPosition(namespacePrefixResolver.getNamespaceURI(nameSpace), absolutePosition));
        }

        absolutePosition++;
        if ((localName == null) || (localName.length() == 0))
        {
            answer.add(new AbsoluteStructuredFieldPosition("*", absolutePosition));
        }
        else
        {
            if(localName.equals("*"))
            {
                answer.add(new AbsoluteStructuredFieldPosition(localName, absolutePosition));
            }
            else
            {
                answer.add(new AbsoluteStructuredFieldPosition(ISO9075.encode(QName.createValidLocalName(ISO9075.decode(localName))), absolutePosition));
            }
        }
        query.appendQuery(answer);

    }

    private void addRelative(String nameSpace, String localName)
    {
        ArrayList<StructuredFieldPosition> answer = new ArrayList<StructuredFieldPosition>(2);
        if ((nameSpace == null) || (nameSpace.length() == 0))
        {
            if(localName.equals("*"))
            {
                answer.add(new RelativeStructuredFieldPosition("*"));
            }
            else if (namespacePrefixResolver.getNamespaceURI("") == null)
            {
                answer.add(new RelativeStructuredFieldPosition(PathTokenFilter.NO_NS_TOKEN_TEXT));
            }
            else if (namespacePrefixResolver.getNamespaceURI("").equals(""))
            {
                answer.add(new RelativeStructuredFieldPosition(PathTokenFilter.NO_NS_TOKEN_TEXT));
            }
            else
            {
                answer.add(new RelativeStructuredFieldPosition(namespacePrefixResolver.getNamespaceURI("")));
            }
        }
        else
        {
            answer.add(new RelativeStructuredFieldPosition(namespacePrefixResolver.getNamespaceURI(nameSpace)));
        }

        if ((localName == null) || (localName.length() == 0))
        {
            answer.add(new RelativeStructuredFieldPosition("*"));
        }
        else
        {
            if(localName.equals("*"))
            {
                answer.add(new RelativeStructuredFieldPosition(localName));
            }
            else
            {
                answer.add(new RelativeStructuredFieldPosition(ISO9075.encode(QName.createValidLocalName(ISO9075.decode(localName)))));
            }
        }
        query.appendQuery(answer);
    }

    public void startOrExpr() throws SAXPathException
    {
        // Do nothing at the moment
    }

    public void startPathExpr() throws SAXPathException
    {
        // Just need one!
    }

    public void startPredicate() throws SAXPathException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void startProcessingInstructionNodeStep(int arg0, String arg1) throws SAXPathException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void startRelationalExpr() throws SAXPathException
    {
        // Do nothing at the moment
    }

    public void startRelativeLocationPath() throws SAXPathException
    {
        isAbsolutePath = false;
    }

    public void startTextNodeStep(int arg0) throws SAXPathException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void startUnaryExpr() throws SAXPathException
    {
        // Do nothing for now
    }

    public void startUnionExpr() throws SAXPathException
    {
        // Do nothing at the moment
    }

    public void startXPath() throws SAXPathException
    {
        query = new PathQuery(dictionaryService);
    }

    public void variableReference(String uri, String localName) throws SAXPathException
    {
       
    }

    /**
     * Set the service to resolve name space prefixes
     * @param namespacePrefixResolver
     */
    public void setNamespacePrefixResolver(NamespacePrefixResolver namespacePrefixResolver)
    {
        this.namespacePrefixResolver = namespacePrefixResolver;
    }

    /**
     * Set the dictionary service.
     * @param dictionaryService
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }
    
    
    

}
