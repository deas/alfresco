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
/*
 * Parser for the Alfresco full text query language.
 * It may be used stand-alone or embedded, for example, in CMIS SQL contains() 
 *
 */

grammar FTS;

options
{
        output    = AST;
        backtrack = false;
}
/*
 * Additional tokens for tree building.
 */


tokens
{
        FTS;
        DISJUNCTION;
        CONJUNCTION;
        NEGATION;
        TERM;
        EXACT_TERM;
        PHRASE;
        EXACT_PHRASE;
        SYNONYM;
        RANGE;
        PROXIMITY;
        DEFAULT;
        MANDATORY;
        OPTIONAL;
        EXCLUDE;
        FIELD_DISJUNCTION;
        FIELD_CONJUNCTION;
        FIELD_NEGATION;
        FIELD_GROUP;
        FIELD_DEFAULT;
        FIELD_MANDATORY;
        FIELD_OPTIONAL;
        FIELD_EXCLUDE;
        FG_TERM;
        FG_EXACT_TERM;
        FG_PHRASE;
        FG_EXACT_PHRASE;
        FG_SYNONYM;
        FG_PROXIMITY;
        FG_RANGE;
        FIELD_REF;
        INCLUSIVE;
        EXCLUSIVE;
        QUALIFIER;
        PREFIX;
        NAME_SPACE;
        BOOST;
        FUZZY;
        TEMPLATE;
}
/*
 * Make sure the lexer and parser are generated in the correct package
 */


@lexer::header
{
package org.alfresco.repo.search.impl.parsers;
}

@header
{
package org.alfresco.repo.search.impl.parsers;
}
/*
 * Embeded java to control the default connective when not specified.
 *
 * Do not support recover from errors
 *
 * Add extra detail to teh error message
 */


@members
{
    public enum Mode
    {
        CMIS, DEFAULT_CONJUNCTION, DEFAULT_DISJUNCTION
    }

    private Stack<String> paraphrases = new Stack<String>();
    
    private boolean defaultFieldConjunction = true;
    
    private Mode mode = Mode.DEFAULT_CONJUNCTION;
    
    public Mode getMode()
    {
       return mode;
    }
    
    public void setMode(Mode mode)
    {
       this.mode = mode;
    }
    
    public boolean defaultFieldConjunction()
    {
       return defaultFieldConjunction;
    }
    
    public void setDefaultFieldConjunction(boolean defaultFieldConjunction)
    {
       this.defaultFieldConjunction = defaultFieldConjunction;
    }
    
    protected Object recoverFromMismatchedToken(IntStream input, int ttype, BitSet follow) throws RecognitionException
    {
        throw new MismatchedTokenException(ttype, input);
    }
        
    public Object recoverFromMismatchedSet(IntStream input, RecognitionException e, BitSet follow) throws RecognitionException
    {
        throw e;
    }
    
   public String getErrorMessage(RecognitionException e, String[] tokenNames) 
    {
        List stack = getRuleInvocationStack(e, this.getClass().getName());
        String msg = e.getMessage();
        if ( e instanceof UnwantedTokenException ) 
            {
            UnwantedTokenException ute = (UnwantedTokenException)e;
            String tokenName="<unknown>";
            if ( ute.expecting== Token.EOF ) 
            {
                tokenName = "EOF";
            }
            else 
            {
                tokenName = tokenNames[ute.expecting];
            }
            msg = "extraneous input " + getTokenErrorDisplay(ute.getUnexpectedToken())
                + " expecting "+tokenName;
        }
        else if ( e instanceof MissingTokenException ) 
        {
            MissingTokenException mte = (MissingTokenException)e;
            String tokenName="<unknown>";
            if ( mte.expecting== Token.EOF ) 
            {
                tokenName = "EOF";
            }
            else 
            {
                tokenName = tokenNames[mte.expecting];
            }
            msg = "missing " + tokenName+" at " + getTokenErrorDisplay(e.token)
                + "  (" + getLongTokenErrorDisplay(e.token) +")";
        }
        else if ( e instanceof MismatchedTokenException ) 
        {
            MismatchedTokenException mte = (MismatchedTokenException)e;
            String tokenName="<unknown>";
            if ( mte.expecting== Token.EOF ) 
            {
                tokenName = "EOF";
            }
            else
            {
                tokenName = tokenNames[mte.expecting];
            }
            msg = "mismatched input " + getTokenErrorDisplay(e.token)
                + " expecting " + tokenName +"  (" + getLongTokenErrorDisplay(e.token) + ")";
        }
        else if ( e instanceof MismatchedTreeNodeException ) 
        {
            MismatchedTreeNodeException mtne = (MismatchedTreeNodeException)e;
            String tokenName="<unknown>";
            if ( mtne.expecting==Token.EOF )  
            {
                tokenName = "EOF";
            }
            else 
            {
                tokenName = tokenNames[mtne.expecting];
            }
            msg = "mismatched tree node: " + mtne.node + " expecting " + tokenName;
        }
        else if ( e instanceof NoViableAltException ) 
        {
            NoViableAltException nvae = (NoViableAltException)e;
            msg = "no viable alternative at input " + getTokenErrorDisplay(e.token)
                + "\n\t (decision=" + nvae.decisionNumber
                + " state " + nvae.stateNumber + ")" 
                + " decision=<<" + nvae.grammarDecisionDescription + ">>";
        }
        else if ( e instanceof EarlyExitException ) 
        {
            //EarlyExitException eee = (EarlyExitException)e;
            // for development, can add "(decision="+eee.decisionNumber+")"
            msg = "required (...)+ loop did not match anything at input " + getTokenErrorDisplay(e.token);
        }
            else if ( e instanceof MismatchedSetException ) 
            {
                MismatchedSetException mse = (MismatchedSetException)e;
                msg = "mismatched input " + getTokenErrorDisplay(e.token)
                + " expecting set " + mse.expecting;
        }
        else if ( e instanceof MismatchedNotSetException ) 
        {
            MismatchedNotSetException mse = (MismatchedNotSetException)e;
            msg = "mismatched input " + getTokenErrorDisplay(e.token)
                + " expecting set " + mse.expecting;
        }
        else if ( e instanceof FailedPredicateException ) 
        {
            FailedPredicateException fpe = (FailedPredicateException)e;
            msg = "rule " + fpe.ruleName + " failed predicate: {" + fpe.predicateText + "}?";
        }
                
        if(paraphrases.size() > 0)
        {
            String paraphrase = (String)paraphrases.peek();
            msg = msg+" "+paraphrase;
        }
        return msg +"\n\t"+stack;
    }
        
    public String getLongTokenErrorDisplay(Token t)
    {
        return t.toString();
    }
    

    public String getErrorString(RecognitionException e)
    {
        String hdr = getErrorHeader(e);
        String msg = getErrorMessage(e, this.getTokenNames());
        return hdr+" "+msg;
    } 
}
/*
 * Always throw exceptions
 */


@rulecatch
{
catch(RecognitionException e)
{
   throw e;
}
}
/*
 * Support for emitting duplicate tokens from the lexer
 * - required to emit ranges after matching floating point literals ...
 */


@lexer::members
{
List tokens = new ArrayList();
public void emit(Token token) {
        state.token = token;
        tokens.add(token);
}
public Token nextToken() {
        nextTokenImpl();
        if ( tokens.size()==0 ) {
            Token eof = new CommonToken((CharStream)input,Token.EOF,
                                            Token.DEFAULT_CHANNEL,
                                            input.index(),input.index());
            eof.setLine(getLine());
            eof.setCharPositionInLine(getCharPositionInLine());
            return eof;
        }
        return (Token)tokens.remove(0);
}

public Token nextTokenImpl() {
        while (true) 
        {
            state.token = null;
            state.channel = Token.DEFAULT_CHANNEL;
            state.tokenStartCharIndex = input.index();
            state.tokenStartCharPositionInLine = input.getCharPositionInLine();
            state.tokenStartLine = input.getLine();
            state.text = null;
            if ( input.LA(1)==CharStream.EOF ) 
            {
                Token eof = new CommonToken((CharStream)input,Token.EOF,
                                            Token.DEFAULT_CHANNEL,
                                            input.index(),input.index());
                eof.setLine(getLine());
                eof.setCharPositionInLine(getCharPositionInLine());
                return eof;
            }
            try 
            {
                mTokens();
                if ( state.token==null ) 
                {
                    emit();
                }
                else if ( state.token==Token.SKIP_TOKEN ) 
                {
                    continue;
                }
                return state.token;
            }
            catch (RecognitionException re) 
            {
                throw new FTSQueryException(getErrorString(re), re);
            }
        }
    }
    
    public String getErrorString(RecognitionException e)
    {
        String hdr = getErrorHeader(e);
        String msg = getErrorMessage(e, this.getTokenNames());
        return hdr+" "+msg;
    }
}


/*
 * Top level query
 */


ftsQuery
        :
        ftsDisjunction EOF
                -> ftsDisjunction
        ;
/*
 * "OR"
 * As SQL, OR has lower precedence than AND
 */


ftsDisjunction
        :
        {getMode() == Mode.CMIS}?=> cmisExplicitDisjunction
        | {getMode() == Mode.DEFAULT_CONJUNCTION}?=> ftsExplicitDisjunction
        | {getMode() == Mode.DEFAULT_DISJUNCTION}?=> ftsImplicitDisjunction
        ;

ftsExplicitDisjunction
        :
        ftsImplicitConjunction (or ftsImplicitConjunction)*
                ->
                        ^(DISJUNCTION ftsImplicitConjunction+)
        ;

cmisExplicitDisjunction
        :
        cmisConjunction (or cmisConjunction)*
                ->
                        ^(DISJUNCTION cmisConjunction+)
        ;

ftsImplicitDisjunction
        :
        (or? ftsExplicitConjunction)+
                ->
                        ^(DISJUNCTION ftsExplicitConjunction+)
        ;
/*
 * "AND"
 */


ftsExplicitConjunction
        :
        ftsPrefixed (and ftsPrefixed)*
                ->
                        ^(CONJUNCTION ftsPrefixed+)
        ;

ftsImplicitConjunction
        :
        (and? ftsPrefixed)+
                ->
                        ^(CONJUNCTION ftsPrefixed+)
        ;

cmisConjunction
        :
        cmisPrefixed+
                ->
                        ^(CONJUNCTION cmisPrefixed+)
        ;
/*
 * Additional info around query compoents 
 * - negation, default, mandatory, optional, exclude and boost
 * These options control how individual elements are embedded in OR and AND
 * and how matches affect the overall score.
 */


ftsPrefixed
        :
        (not) => not ftsTest boost?
                ->
                        ^(NEGATION ftsTest boost?)
        | ftsTest boost?
                ->
                        ^(DEFAULT ftsTest boost?)
        | PLUS ftsTest boost?
                ->
                        ^(MANDATORY ftsTest boost?)
        | BAR ftsTest boost?
                ->
                        ^(OPTIONAL ftsTest boost?)
        | MINUS ftsTest boost?
                ->
                        ^(EXCLUDE ftsTest boost?)
        ;

cmisPrefixed
        :
        cmisTest
                ->
                        ^(DEFAULT cmisTest)
        | MINUS cmisTest
                ->
                        ^(EXCLUDE cmisTest)
        ;
/*
 * Individual query components
 */


ftsTest
        :
        (ftsFieldGroupProximity) => ftsFieldGroupProximity
                ->
                        ^(PROXIMITY ftsFieldGroupProximity)
        | ftsTerm ( (fuzzy) => fuzzy)?
                ->
                        ^(TERM ftsTerm fuzzy?)
        | ftsExactTerm ( (fuzzy) => fuzzy)?
                ->
                        ^(EXACT_TERM ftsExactTerm fuzzy?)
        | ftsPhrase ( (slop) => slop)?
                ->
                        ^(PHRASE ftsPhrase slop?)
        | ftsExactPhrase ( (slop) => slop)?
                ->
                        ^(EXACT_PHRASE ftsExactPhrase slop?)
        | ftsTokenisedPhrase ( (slop) => slop)?
                ->
                        ^(PHRASE ftsTokenisedPhrase slop?)
        | ftsSynonym ( (fuzzy) => fuzzy)?
                ->
                        ^(SYNONYM ftsSynonym fuzzy?)
        | ftsRange
                ->
                        ^(RANGE ftsRange)
        | ftsFieldGroup
                -> ftsFieldGroup
        | LPAREN ftsDisjunction RPAREN
                -> ftsDisjunction
        | template
                -> template
        ;

cmisTest
        :
        cmisTerm
                ->
                        ^(TERM cmisTerm)
        | cmisPhrase
                ->
                        ^(PHRASE cmisPhrase)
        ;

template
        :
        PERCENT tempReference
                ->
                        ^(TEMPLATE tempReference)
        | PERCENT LPAREN (tempReference COMMA?)+ RPAREN
                ->
                        ^(TEMPLATE tempReference+)
        ;

fuzzy
        :
        TILDA number
                ->
                        ^(FUZZY number)
        ;

slop
        :
        TILDA DECIMAL_INTEGER_LITERAL
                ->
                        ^(FUZZY DECIMAL_INTEGER_LITERAL)
        ;

boost
        :
        CARAT number
                ->
                        ^(BOOST number)
        ;

ftsTerm
        :
        (fieldReference COLON)? ftsWord
                -> ftsWord fieldReference?
        ;

cmisTerm
        :
        ftsWord
                -> ftsWord
        ;

ftsExactTerm
        :
        EQUALS ftsTerm
                -> ftsTerm
        ;

ftsPhrase
        :
        (fieldReference COLON)? FTSPHRASE
                -> FTSPHRASE fieldReference?
        ;
        
ftsExactPhrase
        :
        EQUALS ftsPhrase
                -> ftsPhrase
        ;
        
ftsTokenisedPhrase
        :
        TILDA ftsPhrase
                -> ftsPhrase
        ;


cmisPhrase
        :
        FTSPHRASE
                -> FTSPHRASE
        ;

ftsSynonym
        :
        TILDA ftsTerm
                -> ftsTerm
        ;

ftsRange
        :
        (fieldReference COLON)? ftsFieldGroupRange
                -> ftsFieldGroupRange fieldReference?
        ;

ftsFieldGroup
        :
        fieldReference COLON LPAREN ftsFieldGroupDisjunction RPAREN
                ->
                        ^(FIELD_GROUP fieldReference ftsFieldGroupDisjunction)
        ;

ftsFieldGroupDisjunction
        :
        {defaultFieldConjunction() == true}?=> ftsFieldGroupExplicitDisjunction
        | {defaultFieldConjunction() == false}?=> ftsFieldGroupImplicitDisjunction
        ;

ftsFieldGroupExplicitDisjunction
        :
        ftsFieldGroupImplicitConjunction (or ftsFieldGroupImplicitConjunction)*
                ->
                        ^(FIELD_DISJUNCTION ftsFieldGroupImplicitConjunction+)
        ;

ftsFieldGroupImplicitDisjunction
        :
        (or? ftsFieldGroupExplicitConjunction)+
                ->
                        ^(FIELD_DISJUNCTION ftsFieldGroupExplicitConjunction+)
        ;
/*
 * "AND"
 */


ftsFieldGroupExplicitConjunction
        :
        ftsFieldGroupPrefixed (and ftsFieldGroupPrefixed)*
                ->
                        ^(FIELD_CONJUNCTION ftsFieldGroupPrefixed+)
        ;

ftsFieldGroupImplicitConjunction
        :
        (and? ftsFieldGroupPrefixed)+
                ->
                        ^(FIELD_CONJUNCTION ftsFieldGroupPrefixed+)
        ;

ftsFieldGroupPrefixed
        :
        (not) => not ftsFieldGroupTest boost?
                ->
                        ^(FIELD_NEGATION ftsFieldGroupTest boost?)
        | ftsFieldGroupTest boost?
                ->
                        ^(FIELD_DEFAULT ftsFieldGroupTest boost?)
        | PLUS ftsFieldGroupTest boost?
                ->
                        ^(FIELD_MANDATORY ftsFieldGroupTest boost?)
        | BAR ftsFieldGroupTest boost?
                ->
                        ^(FIELD_OPTIONAL ftsFieldGroupTest boost?)
        | MINUS ftsFieldGroupTest boost?
                ->
                        ^(FIELD_EXCLUDE ftsFieldGroupTest boost?)
        ;

ftsFieldGroupTest
        :
        (ftsFieldGroupProximity) => ftsFieldGroupProximity
                ->
                        ^(FG_PROXIMITY ftsFieldGroupProximity)
        | ftsFieldGroupTerm ( (fuzzy) => fuzzy)?
                ->
                        ^(FG_TERM ftsFieldGroupTerm fuzzy?)
        | ftsFieldGroupExactTerm ( (fuzzy) => fuzzy)?
                ->
                        ^(FG_EXACT_TERM ftsFieldGroupExactTerm fuzzy?)
        | ftsFieldGroupPhrase ( (slop) => slop)?
                ->
                        ^(FG_PHRASE ftsFieldGroupPhrase slop?)
        | ftsFieldGroupExactPhrase ( (slop) => slop)?
                ->
                        ^(FG_EXACT_PHRASE ftsFieldGroupExactPhrase slop?)
        | ftsFieldGroupTokenisedPhrase ( (slop) => slop)?
                ->
                        ^(FG_PHRASE ftsFieldGroupTokenisedPhrase slop?)
        | ftsFieldGroupSynonym ( (fuzzy) => fuzzy)?
                ->
                        ^(FG_SYNONYM ftsFieldGroupSynonym fuzzy?)
        | ftsFieldGroupRange
                ->
                        ^(FG_RANGE ftsFieldGroupRange)
        | LPAREN ftsFieldGroupDisjunction RPAREN
                -> ftsFieldGroupDisjunction
        ;

ftsFieldGroupTerm
        :
        ftsWord
        ;

ftsFieldGroupExactTerm
        :
        EQUALS ftsFieldGroupTerm
                -> ftsFieldGroupTerm
        ;

ftsFieldGroupPhrase
        :
        FTSPHRASE
        ;
        
ftsFieldGroupExactPhrase
        :
        EQUALS ftsFieldGroupExactPhrase
                -> ftsFieldGroupExactPhrase
        ;
        
ftsFieldGroupTokenisedPhrase
        :
        TILDA ftsFieldGroupExactPhrase
                -> ftsFieldGroupExactPhrase
        ;

ftsFieldGroupSynonym
        :
        TILDA ftsFieldGroupTerm
                -> ftsFieldGroupTerm
        ;

ftsFieldGroupProximity
        :
        ftsFieldGroupProximityTerm ( (proximityGroup) => proximityGroup ftsFieldGroupProximityTerm)+
                -> ftsFieldGroupProximityTerm (proximityGroup ftsFieldGroupProximityTerm)+
        ;

ftsFieldGroupProximityTerm
        :
        ID
        | FTSWORD
        | FTSPRE
        | FTSWILD
        | NOT
        | TO
        | DECIMAL_INTEGER_LITERAL
        | FLOATING_POINT_LITERAL
        ;

proximityGroup
        :
        STAR (LPAREN DECIMAL_INTEGER_LITERAL? RPAREN)?
                ->
                        ^(PROXIMITY DECIMAL_INTEGER_LITERAL?)
        ;

ftsFieldGroupRange
        :
        ftsRangeWord DOTDOT ftsRangeWord
                -> INCLUSIVE ftsRangeWord ftsRangeWord INCLUSIVE
        | range_left ftsRangeWord TO ftsRangeWord range_right
                -> range_left ftsRangeWord ftsRangeWord range_right
        ;

range_left
        :
        LSQUARE
                -> INCLUSIVE
        | LT
                -> EXCLUSIVE
        ;

range_right
        :
        RSQUARE
                -> INCLUSIVE
        | GT
                -> EXCLUSIVE
        ;

/* Need to fix the generated parser for extra COLON check ??*/

fieldReference
        :
        AT?
        (
                prefix
                | uri
        )?
        identifier
                ->
                        ^(FIELD_REF identifier prefix? uri?)
        ;

tempReference
        :
        AT?
        (
                prefix
                | uri
        )?
        identifier
                ->
                        ^(FIELD_REF identifier prefix? uri?)
        ;

prefix
        :
        identifier COLON
                ->
                        ^(PREFIX identifier)
        ;

uri
        :
        URI
                ->
                        ^(NAME_SPACE URI)
        ;

identifier
        :
        ID
                ->
                        ID
        | id1=ID DOT id2=ID
                ->      {new CommonTree(new CommonToken(FTSLexer.ID, $id1.text+$DOT.text+$id2.text))}
        ;

ftsWord
        :
        ID
        | FTSWORD
        | FTSPRE
        | FTSWILD
        | NOT
        | TO
        | DECIMAL_INTEGER_LITERAL
        | FLOATING_POINT_LITERAL
        | STAR
        | QUESTION_MARK
        ;

number
        :
        DECIMAL_INTEGER_LITERAL
        | FLOATING_POINT_LITERAL
        ;

ftsRangeWord
        :
        ID
        | FTSWORD
        | FTSPRE
        | FTSWILD
        | FTSPHRASE
        | DECIMAL_INTEGER_LITERAL
        | FLOATING_POINT_LITERAL
        ;

//

or
        :
        OR
        | BAR BAR
        ;

and
        :
        AND
        | AMP AMP
        ;

not
        :
        NOT
        | EXCLAMATION
        ;

// ===== //
// LEXER //
// ===== //

FTSPHRASE
        :
        '"'
        (
                F_ESC
                |
                ~(
                        '\\'
                        | '"'
                 )
        )*
        '"'
        | '\''
        (
                F_ESC
                |
                ~(
                        '\\'
                        | '\''
                 )
        )*
        '\''
        ;
/*
 * Basic URI pattern based on the regular expression patttern taken from the RFC (it it not full URI parsing)
 * Note this means the language can not use {} anywhere else in the syntax
 */


URI
        :
        '{'
        (
                (
                        F_URI_ALPHA
                        | F_URI_DIGIT
                        | F_URI_OTHER
                )
                        =>
                (
                        F_URI_ALPHA
                        | F_URI_DIGIT
                        | F_URI_OTHER
                )+
                COLON
        )?
        (
                ( ('//') => '//')
                (
                        (
                                F_URI_ALPHA
                                | F_URI_DIGIT
                                | F_URI_OTHER
                                | COLON
                        )
                                =>
                        (
                                F_URI_ALPHA
                                | F_URI_DIGIT
                                | F_URI_OTHER
                                | COLON
                        )
                )*
        )?
        (
                F_URI_ALPHA
                | F_URI_DIGIT
                | F_URI_OTHER
                | COLON
                | '/'
        )*
        (
                '?'
                (
                        F_URI_ALPHA
                        | F_URI_DIGIT
                        | F_URI_OTHER
                        | COLON
                        | '/'
                        | '?'
                )*
        )?
        (
                '#'
                (
                        F_URI_ALPHA
                        | F_URI_DIGIT
                        | F_URI_OTHER
                        | COLON
                        | '/'
                        | '?'
                        | '#'
                )*
        )?
        '}'
        ;

fragment
F_URI_ALPHA
        :
        'A'..'Z'
        | 'a'..'z'
        ;

fragment
F_URI_DIGIT
        :
        '0'..'9'
        ;

fragment
F_URI_ESC
        :
        '%' F_HEX F_HEX
        ;

fragment
F_URI_OTHER
        :
        '-'
        | '.'
        | '_'
        | '~'
        | '['
        | ']'
        | '@'
        | '!'
        | '$'
        | '&'
        | '\''
        | '('
        | ')'
        | '*'
        | '+'
        | ','
        | ';'
        | '='
        ;
/*
 * Simple tokens, note all are case insensitive
 */


OR
        :
        (
                'O'
                | 'o'
        )
        (
                'R'
                | 'r'
        )
        ;

AND
        :
        (
                'A'
                | 'a'
        )
        (
                'N'
                | 'n'
        )
        (
                'D'
                | 'd'
        )
        ;

NOT
        :
        (
                'N'
                | 'n'
        )
        (
                'O'
                | 'o'
        )
        (
                'T'
                | 't'
        )
        ;

TILDA
        :
        '~'
        ;

LPAREN
        :
        '('
        ;

RPAREN
        :
        ')'
        ;

PLUS
        :
        '+'
        ;

MINUS
        :
        '-'
        ;

COLON
        :
        ':'
        ;

STAR
        :
        '*'
        ;

DOTDOT
        :
        '..'
        ;

DOT
        :
        '.'
        ;

AMP
        :
        '&'
        ;

EXCLAMATION
        :
        '!'
        ;

BAR
        :
        '|'
        ;

EQUALS
        :
        '='
        ;

QUESTION_MARK
        :
        '?'
        ;

LCURL
        :
        '{'
        ;

RCURL
        :
        '}'
        ;

LSQUARE
        :
        '['
        ;

RSQUARE
        :
        ']'
        ;

TO
        :
        (
                'T'
                | 't'
        )
        (
                'O'
                | 'o'
        )
        ;

COMMA
        :
        ','
        ;

CARAT
        :
        '^'
        ;

DOLLAR
        :
        '$'
        ;

GT
        :
        '>'
        ;

LT
        :
        '<'
        ;

AT
        :
        '@'
        ;

PERCENT
        :
        '%'
        ;

/**
 * ID
 * _x????_ encoding is supported for invalid sql characters but requires nothing here, they are handled in the code 
 * Also supports \ style escaping for non CMIS SQL 
 */
ID
        :
        (
                'a'..'z'
                | 'A'..'Z'
                | '_'
        )
        (
                'a'..'z'
                | 'A'..'Z'
                | '0'..'9'
                | '_'
                | '$'
                | '#'
                | F_ESC
        )*
        ;

DECIMAL_INTEGER_LITERAL
        :
        (
                PLUS
                | MINUS
        )?
        DECIMAL_NUMERAL
        ;

FTSWORD
        :
        (
                F_ESC
                | INWORD
        )+
        ;

FTSPRE
        :
        (
                F_ESC
                | INWORD
        )+
        STAR
        ;

FTSWILD
        :
        (
                F_ESC
                | INWORD
                | STAR
                | QUESTION_MARK
        )+
        ;

fragment
F_ESC
        :
        '\\'
        (
                // unicode
                'u' F_HEX F_HEX F_HEX F_HEX
                // any single char escaped
                | .
        )
        ;

fragment
F_HEX
        :
        '0'..'9'
        | 'a'..'f'
        | 'A'..'F'
        ;

fragment
INWORD
        :  // Generated from Java Character.isLetterOrDigit()
          '\u0030'..'\u0039'
        | '\u0041'..'\u005a'
        | '\u0061'..'\u007a'
        | '\u00aa'
        | '\u00b5'
        | '\u00ba'
        | '\u00c0'..'\u00d6'
        | '\u00d8'..'\u00f6'
        | '\u00f8'..'\u0236'
        | '\u0250'..'\u02c1'
        | '\u02c6'..'\u02d1'
        | '\u02e0'..'\u02e4'
        | '\u02ee'
        | '\u037a'
        | '\u0386'
        | '\u0388'..'\u038a'
        | '\u038c'
        | '\u038e'..'\u03a1'
        | '\u03a3'..'\u03ce'
        | '\u03d0'..'\u03f5'
        | '\u03f7'..'\u03fb'
        | '\u0400'..'\u0481'
        | '\u048a'..'\u04ce'
        | '\u04d0'..'\u04f5'
        | '\u04f8'..'\u04f9'
        | '\u0500'..'\u050f'
        | '\u0531'..'\u0556'
        | '\u0559'
        | '\u0561'..'\u0587'
        | '\u05d0'..'\u05ea'
        | '\u05f0'..'\u05f2'
        | '\u0621'..'\u063a'
        | '\u0640'..'\u064a'
        | '\u0660'..'\u0669'
        | '\u066e'..'\u066f'
        | '\u0671'..'\u06d3'
        | '\u06d5'
        | '\u06e5'..'\u06e6'
        | '\u06ee'..'\u06fc'
        | '\u06ff'
        | '\u0710'
        | '\u0712'..'\u072f'
        | '\u074d'..'\u074f'
        | '\u0780'..'\u07a5'
        | '\u07b1'
        | '\u0904'..'\u0939'
        | '\u093d'
        | '\u0950'
        | '\u0958'..'\u0961'
        | '\u0966'..'\u096f'
        | '\u0985'..'\u098c'
        | '\u098f'..'\u0990'
        | '\u0993'..'\u09a8'
        | '\u09aa'..'\u09b0'
        | '\u09b2'
        | '\u09b6'..'\u09b9'
        | '\u09bd'
        | '\u09dc'..'\u09dd'
        | '\u09df'..'\u09e1'
        | '\u09e6'..'\u09f1'
        | '\u0a05'..'\u0a0a'
        | '\u0a0f'..'\u0a10'
        | '\u0a13'..'\u0a28'
        | '\u0a2a'..'\u0a30'
        | '\u0a32'..'\u0a33'
        | '\u0a35'..'\u0a36'
        | '\u0a38'..'\u0a39'
        | '\u0a59'..'\u0a5c'
        | '\u0a5e'
        | '\u0a66'..'\u0a6f'
        | '\u0a72'..'\u0a74'
        | '\u0a85'..'\u0a8d'
        | '\u0a8f'..'\u0a91'
        | '\u0a93'..'\u0aa8'
        | '\u0aaa'..'\u0ab0'
        | '\u0ab2'..'\u0ab3'
        | '\u0ab5'..'\u0ab9'
        | '\u0abd'
        | '\u0ad0'
        | '\u0ae0'..'\u0ae1'
        | '\u0ae6'..'\u0aef'
        | '\u0b05'..'\u0b0c'
        | '\u0b0f'..'\u0b10'
        | '\u0b13'..'\u0b28'
        | '\u0b2a'..'\u0b30'
        | '\u0b32'..'\u0b33'
        | '\u0b35'..'\u0b39'
        | '\u0b3d'
        | '\u0b5c'..'\u0b5d'
        | '\u0b5f'..'\u0b61'
        | '\u0b66'..'\u0b6f'
        | '\u0b71'
        | '\u0b83'
        | '\u0b85'..'\u0b8a'
        | '\u0b8e'..'\u0b90'
        | '\u0b92'..'\u0b95'
        | '\u0b99'..'\u0b9a'
        | '\u0b9c'
        | '\u0b9e'..'\u0b9f'
        | '\u0ba3'..'\u0ba4'
        | '\u0ba8'..'\u0baa'
        | '\u0bae'..'\u0bb5'
        | '\u0bb7'..'\u0bb9'
        | '\u0be7'..'\u0bef'
        | '\u0c05'..'\u0c0c'
        | '\u0c0e'..'\u0c10'
        | '\u0c12'..'\u0c28'
        | '\u0c2a'..'\u0c33'
        | '\u0c35'..'\u0c39'
        | '\u0c60'..'\u0c61'
        | '\u0c66'..'\u0c6f'
        | '\u0c85'..'\u0c8c'
        | '\u0c8e'..'\u0c90'
        | '\u0c92'..'\u0ca8'
        | '\u0caa'..'\u0cb3'
        | '\u0cb5'..'\u0cb9'
        | '\u0cbd'
        | '\u0cde'
        | '\u0ce0'..'\u0ce1'
        | '\u0ce6'..'\u0cef'
        | '\u0d05'..'\u0d0c'
        | '\u0d0e'..'\u0d10'
        | '\u0d12'..'\u0d28'
        | '\u0d2a'..'\u0d39'
        | '\u0d60'..'\u0d61'
        | '\u0d66'..'\u0d6f'
        | '\u0d85'..'\u0d96'
        | '\u0d9a'..'\u0db1'
        | '\u0db3'..'\u0dbb'
        | '\u0dbd'
        | '\u0dc0'..'\u0dc6'
        | '\u0e01'..'\u0e30'
        | '\u0e32'..'\u0e33'
        | '\u0e40'..'\u0e46'
        | '\u0e50'..'\u0e59'
        | '\u0e81'..'\u0e82'
        | '\u0e84'
        | '\u0e87'..'\u0e88'
        | '\u0e8a'
        | '\u0e8d'
        | '\u0e94'..'\u0e97'
        | '\u0e99'..'\u0e9f'
        | '\u0ea1'..'\u0ea3'
        | '\u0ea5'
        | '\u0ea7'
        | '\u0eaa'..'\u0eab'
        | '\u0ead'..'\u0eb0'
        | '\u0eb2'..'\u0eb3'
        | '\u0ebd'
        | '\u0ec0'..'\u0ec4'
        | '\u0ec6'
        | '\u0ed0'..'\u0ed9'
        | '\u0edc'..'\u0edd'
        | '\u0f00'
        | '\u0f20'..'\u0f29'
        | '\u0f40'..'\u0f47'
        | '\u0f49'..'\u0f6a'
        | '\u0f88'..'\u0f8b'
        | '\u1000'..'\u1021'
        | '\u1023'..'\u1027'
        | '\u1029'..'\u102a'
        | '\u1040'..'\u1049'
        | '\u1050'..'\u1055'
        | '\u10a0'..'\u10c5'
        | '\u10d0'..'\u10f8'
        | '\u1100'..'\u1159'
        | '\u115f'..'\u11a2'
        | '\u11a8'..'\u11f9'
        | '\u1200'..'\u1206'
        | '\u1208'..'\u1246'
        | '\u1248'
        | '\u124a'..'\u124d'
        | '\u1250'..'\u1256'
        | '\u1258'
        | '\u125a'..'\u125d'
        | '\u1260'..'\u1286'
        | '\u1288'
        | '\u128a'..'\u128d'
        | '\u1290'..'\u12ae'
        | '\u12b0'
        | '\u12b2'..'\u12b5'
        | '\u12b8'..'\u12be'
        | '\u12c0'
        | '\u12c2'..'\u12c5'
        | '\u12c8'..'\u12ce'
        | '\u12d0'..'\u12d6'
        | '\u12d8'..'\u12ee'
        | '\u12f0'..'\u130e'
        | '\u1310'
        | '\u1312'..'\u1315'
        | '\u1318'..'\u131e'
        | '\u1320'..'\u1346'
        | '\u1348'..'\u135a'
        | '\u1369'..'\u1371'
        | '\u13a0'..'\u13f4'
        | '\u1401'..'\u166c'
        | '\u166f'..'\u1676'
        | '\u1681'..'\u169a'
        | '\u16a0'..'\u16ea'
        | '\u1700'..'\u170c'
        | '\u170e'..'\u1711'
        | '\u1720'..'\u1731'
        | '\u1740'..'\u1751'
        | '\u1760'..'\u176c'
        | '\u176e'..'\u1770'
        | '\u1780'..'\u17b3'
        | '\u17d7'
        | '\u17dc'
        | '\u17e0'..'\u17e9'
        | '\u1810'..'\u1819'
        | '\u1820'..'\u1877'
        | '\u1880'..'\u18a8'
        | '\u1900'..'\u191c'
        | '\u1946'..'\u196d'
        | '\u1970'..'\u1974'
        | '\u1d00'..'\u1d6b'
        | '\u1e00'..'\u1e9b'
        | '\u1ea0'..'\u1ef9'
        | '\u1f00'..'\u1f15'
        | '\u1f18'..'\u1f1d'
        | '\u1f20'..'\u1f45'
        | '\u1f48'..'\u1f4d'
        | '\u1f50'..'\u1f57'
        | '\u1f59'
        | '\u1f5b'
        | '\u1f5d'
        | '\u1f5f'..'\u1f7d'
        | '\u1f80'..'\u1fb4'
        | '\u1fb6'..'\u1fbc'
        | '\u1fbe'
        | '\u1fc2'..'\u1fc4'
        | '\u1fc6'..'\u1fcc'
        | '\u1fd0'..'\u1fd3'
        | '\u1fd6'..'\u1fdb'
        | '\u1fe0'..'\u1fec'
        | '\u1ff2'..'\u1ff4'
        | '\u1ff6'..'\u1ffc'
        | '\u2071'
        | '\u207f'
        | '\u2102'
        | '\u2107'
        | '\u210a'..'\u2113'
        | '\u2115'
        | '\u2119'..'\u211d'
        | '\u2124'
        | '\u2126'
        | '\u2128'
        | '\u212a'..'\u212d'
        | '\u212f'..'\u2131'
        | '\u2133'..'\u2139'
        | '\u213d'..'\u213f'
        | '\u2145'..'\u2149'
        | '\u3005'..'\u3006'
        | '\u3031'..'\u3035'
        | '\u303b'..'\u303c'
        | '\u3041'..'\u3096'
        | '\u309d'..'\u309f'
        | '\u30a1'..'\u30fa'
        | '\u30fc'..'\u30ff'
        | '\u3105'..'\u312c'
        | '\u3131'..'\u318e'
        | '\u31a0'..'\u31b7'
        | '\u31f0'..'\u31ff'
        | '\u3400'..'\u4db5'
        | '\u4e00'..'\u9fa5'
        | '\ua000'..'\ua48c'
        | '\uac00'..'\ud7a3'
        | '\uf900'..'\ufa2d'
        | '\ufa30'..'\ufa6a'
        | '\ufb00'..'\ufb06'
        | '\ufb13'..'\ufb17'
        | '\ufb1d'
        | '\ufb1f'..'\ufb28'
        | '\ufb2a'..'\ufb36'
        | '\ufb38'..'\ufb3c'
        | '\ufb3e'
        | '\ufb40'..'\ufb41'
        | '\ufb43'..'\ufb44'
        | '\ufb46'..'\ufbb1'
        | '\ufbd3'..'\ufd3d'
        | '\ufd50'..'\ufd8f'
        | '\ufd92'..'\ufdc7'
        | '\ufdf0'..'\ufdfb'
        | '\ufe70'..'\ufe74'
        | '\ufe76'..'\ufefc'
        | '\uff10'..'\uff19'
        | '\uff21'..'\uff3a'
        | '\uff41'..'\uff5a'
        | '\uff66'..'\uffbe'
        | '\uffc2'..'\uffc7'
        | '\uffca'..'\uffcf'
        | '\uffd2'..'\uffd7'
        | '\uffda'..'\uffdc'
        ;
/*
 * Range and floating point have to be conbined to avoid lexer issues.
 * This requires multi-token emits and addition supporting java code - see above ...
 *
 * Special rules for the likes of
 * 1..  integer ranges
 * 1... float range with the float terminated by .
 * If floats are 'full' e.g. 2.4.. then the parse matches the normal float tokem and a DOTDOT token
 * Likewise .1...2 does not require any special support
 *
 * Float and integer are based on the Java language spec.
 */


FLOATING_POINT_LITERAL
        // Integer ranges
        :
        d=START_RANGE_I r=DOTDOT {
      			$d.setType(DECIMAL_INTEGER_LITERAL);
      			emit($d);
      			$r.setType(DOTDOT);
      			emit($r);
    		}
        // Float ranges
        | d=START_RANGE_F r=DOTDOT {
      			$d.setType(FLOATING_POINT_LITERAL);
      			emit($d);
      			$r.setType(DOTDOT);
      			emit($r);
    		}
        // Normal float rules
        |
        (
                PLUS
                | MINUS
        )?
        DIGIT+ DOT DIGIT* EXPONENT?
        |
        (
                PLUS
                | MINUS
        )?
        DOT DIGIT+ EXPONENT?
        |
        (
                PLUS
                | MINUS
        )?
        DIGIT+ EXPONENT
        ;

fragment
START_RANGE_I
        :
        (
                PLUS
                | MINUS
        )?
        DIGIT+
        ;

fragment
START_RANGE_F
        :
        (
                PLUS
                | MINUS
        )?
        DIGIT+ DOT
        ;

/**   
 * Fragments for decimal
 */
fragment
DECIMAL_NUMERAL
        :
        ZERO_DIGIT
        | NON_ZERO_DIGIT DIGIT*
        ;

fragment
DIGIT
        :
        ZERO_DIGIT
        | NON_ZERO_DIGIT
        ;

fragment
ZERO_DIGIT
        :
        '0'
        ;

fragment
NON_ZERO_DIGIT
        :
        '1'..'9'
        ;

fragment
E
        :
        (
                'e'
                | 'E'
        )
        ;

fragment
EXPONENT
        :
        E SIGNED_INTEGER
        ;

fragment
SIGNED_INTEGER
        :
        (
                PLUS
                | MINUS
        )?
        DIGIT+
        ;
/*
 * Standard white space
 * White space may be escaped by \ in some tokens 
 */


WS
        :
        (
                ' '
                | '\t'
                | '\r'
                | '\n'
                | '\u000C'   // FF
                | '\u00a0'   // Additional Unicode space from Character.isSpaceChar()
                | '\u1680'
                | '\u180e'
                | '\u2000' ..  '\u200b'
                | '\u2028' ..  '\u2029'
                | '\u202f'
                | '\u205f'
                | '\u3000'
        )+
        { $channel = HIDDEN; }
        ;
