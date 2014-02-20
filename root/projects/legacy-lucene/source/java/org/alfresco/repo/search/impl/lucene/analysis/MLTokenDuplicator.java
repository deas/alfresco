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
package org.alfresco.repo.search.impl.lucene.analysis;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

import org.alfresco.repo.search.MLAnalysisMode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

/**
 * Create duplicate tokens for multilingual varients The forms are Tokens: Token - all languages {fr}Token - if a
 * language is specified {fr_CA}Token - if a language and country is specified {fr_CA_Varient}Token - for all three
 * {fr__Varient}Token - for a language varient with no country
 * 
 * @author andyh
 */
public class MLTokenDuplicator extends Tokenizer
{
    private static Log    s_logger = LogFactory.getLog(MLTokenDuplicator.class);
    
    TokenStream source;

    Locale locale;

    Iterator<Token> it;

    HashSet<String> prefixes;

    public MLTokenDuplicator(TokenStream source, Locale locale, Reader reader, MLAnalysisMode mlAnalaysisMode)
    {
        super(reader);
        this.source = source;
        this.locale = locale;

        Collection<Locale> locales = MLAnalysisMode.getLocales(mlAnalaysisMode, locale, false);
        prefixes = new HashSet<String>(locales.size());
        for(Locale toAdd : locales)
        {
            String localeString = toAdd.toString();
            if(localeString.length() == 0)
            {
                prefixes.add("");
            }
            else
            {
                StringBuilder builder = new StringBuilder(16);
                builder.append("{").append(localeString).append("}");
                prefixes.add(builder.toString());
            }
        }
        if(s_logger.isDebugEnabled())
        {
            s_logger.debug("Locale "+ locale +" using "+mlAnalaysisMode+" is "+prefixes);
        }

    }
    
    public MLTokenDuplicator(Locale locale, MLAnalysisMode mlAnalaysisMode)
    {
        this(null, locale, null, mlAnalaysisMode);
    }

    @Override
    public Token next() throws IOException
    {
        Token t = null;
        if (it == null)
        {
            it = buildIterator();
        }
        if (it == null)
        {
            return null;
        }
        if (it.hasNext())
        {
            t = it.next();
            return t;
        }
        else
        {
            it = null;
            t = this.next();
            return t;
        }
    }

    private Iterator<Token> buildIterator() throws IOException
    {
        Token token = source.next();
        return buildIterator(token);

    }


    public Iterator<Token> buildIterator(Token token)
    {
        if (token == null)
        {
            return null;
        }

        ArrayList<Token> tokens = new ArrayList<Token>(prefixes.size());
        for (String prefix : prefixes)
        {
            Token newToken = new Token(prefix + token.termText(), token.startOffset(), token.endOffset(), token.type());
            if (tokens.size() == 0)
            {
                newToken.setPositionIncrement(token.getPositionIncrement());
            }
            else
            {
                newToken.setPositionIncrement(0);
            }
            tokens.add(newToken);
        }
        return tokens.iterator();

    }

    
}
