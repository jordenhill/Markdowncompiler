package jordenhill.project1.implementation;

import java.util.Stack;

//A syntax analyzer
//Gets tokens from the lexical analyzer and checks that the syntax is valid, reporting any syntax errors.
//Adds valid tokens to form parse tree, which is returned to the semantic analyzer
public class SyntaxAnalyzer implements jordenhill.project1.interfaces.SyntaxAnalyzer {
    private Stack<String> parseTree = new Stack<>();

    public Stack<String> getParseTree(){
        return parseTree;
    }

    //BNF for markdown language
    //<markdown> ::= DOCB <variable-define> <head> <body> DOCE
    public void markdown() throws CompilerException {
        if(Compiler.currentToken.equalsIgnoreCase(Token.DOCB)) { //DOCB
            parseTree.push(Compiler.currentToken);
            Compiler.lexer.getNextToken();
            variableDefine(); //<variable-define>
            head(); //<head>
            body(); //<body>

            if(Compiler.currentToken.equalsIgnoreCase(Token.DOCE)){ //DOCE
                parseTree.push(Compiler.currentToken);
                Compiler.lexer.getNextToken();
                if(Compiler.currentToken != null) {
                    throw new CompilerException("Unexpected token past end of document: " + Compiler.currentToken);
                }
            }
            else {
                throw new CompilerException("Unexpected token: " + Compiler.currentToken);
            }
        }
        else {
            throw new CompilerException("Unexpected token: " + Compiler.currentToken);

        }
    }

    //Check if token is a text token or a markdown token
    private boolean tokenIsText(String text){
        for (String s : Token.tokens) {
            if (s.equalsIgnoreCase(text)) {
                return false;
            }
        }
        return true;
    }

    //BNF for head rule
    //<head> ::= HEAD <title> HEAD | E
    public void head() throws CompilerException {
        if(Compiler.currentToken.equalsIgnoreCase(Token.HEAD)){ //HEAD
            parseTree.push(Compiler.currentToken);
            Compiler.lexer.getNextToken();

            if(Compiler.currentToken.equalsIgnoreCase(Token.TITLEB)){ //<title>
                title();
            }
            else {
                throw new CompilerException("Unexpected token: " + Compiler.currentToken);
            }

            if(Compiler.currentToken.equalsIgnoreCase(Token.HEAD)){ //HEAD
                parseTree.push(Compiler.currentToken);
                Compiler.lexer.getNextToken();
            }
            else {
                throw new CompilerException("Unexpected token: " + Compiler.currentToken);
            }
        }

        //If no cases apply assume empty set and continue on
    }

    //BNF for title rule
    //<title> ::= TITLEB TEXT TITLEE  E
    public void title() throws CompilerException {
        if(Compiler.currentToken.equalsIgnoreCase(Token.TITLEB)){
            parseTree.push(Compiler.currentToken);
            Compiler.lexer.getNextToken();

            while(tokenIsText(Compiler.currentToken)){ //TEXT
                parseTree.push(Compiler.currentToken);
                Compiler.lexer.getNextToken();
            }

            if(Compiler.currentToken.equalsIgnoreCase(Token.TITLEE)){ //TITLEE
                parseTree.push(Compiler.currentToken);
                Compiler.lexer.getNextToken();
            }
            else {
                throw new CompilerException("Unexpected token: " + Compiler.currentToken);
            }
        }

        //If no cases apply assume empty set and continue on.
    }

    //BNF for body rule
    /*<body> ::= <inner-text> <body> |
                 <paragraph> <body> |
                 <new-line> <body> | E
     */
    public void body() throws CompilerException {
        String token = Compiler.currentToken;

        if(token.equalsIgnoreCase(Token.USEB) || token.equalsIgnoreCase(Token.BOLD)
                || token.equalsIgnoreCase(Token.ITALICS)
                || token.equalsIgnoreCase(Token.LISTITEMB)
                || token.equalsIgnoreCase(Token.AUDIO)
                || token.equalsIgnoreCase(Token.VIDEO)
                || token.equalsIgnoreCase(Token.LINKB)
                || token.equalsIgnoreCase(Token.NEWLINE)
                || tokenIsText(token)){ //<inner-text> <body>
            innerText();
            body();
        }
        else if(token.equalsIgnoreCase(Token.PARAB)){ //<paragraph> <body>
            paragraph();
            body();
        }
        else if(token.equalsIgnoreCase(Token.NEWLINE)){ //<new-line> <body>
            newline();
        }

        //If no cases apply assume empty set and continue on
    }

    //BNF for paragraph rule
    //<paragraph> ::= PARAB <variable-define> <inner-text> PARAE
    public void paragraph() throws CompilerException {
        if(Compiler.currentToken.equalsIgnoreCase(Token.PARAB)){ //PARAB
            parseTree.push(Compiler.currentToken);
            Compiler.lexer.getNextToken();

            variableDefine(); //<variable-define>
            innerText(); //<inner-text>

            if(Compiler.currentToken.equalsIgnoreCase(Token.PARAE)){ //PARAE
                parseTree.push(Compiler.currentToken);
                Compiler.lexer.getNextToken();
            } else {
                throw new CompilerException("Unexpected token: " + Compiler.currentToken);
            }
        }
        else {
            throw new CompilerException("Unexpected token: " + Compiler.currentToken);
        }
    }

    //BNF rule for <inner-text>
    /*<inner-text> := <variable-use> <inner-text> |
                      <bold> <inner-text> |
                      <list-item> <inner-text> |
                      <italics> <inner-text> |
                      <audio> <inner-text> |
                      <video> <inner-text> |
                      <link> <inner-text> |
                      <new-line> <inner-text> |
                      TEXT <inner-text> | E
    */
    public void innerText() throws CompilerException {
        String token = Compiler.currentToken;

        if(token.equalsIgnoreCase(Token.USEB)) { //<variable-use> <inner-text>
            variableUse();
            innerText();
        }
        else if(token.equalsIgnoreCase(Token.BOLD)) { //<bold> <inner-text>
            bold();
            innerText();
        }
        else if(token.equalsIgnoreCase(Token.ITALICS)) { //<italics> <inner-text>
            italics();
            innerText();
        }
        else if(token.equalsIgnoreCase(Token.LISTITEMB)) { //<list-item> <inner-text>
            listItem();
            innerText();
        }
        else if(token.equalsIgnoreCase(Token.AUDIO)) { //<audio> <inner-text>
            audio();
            innerText();
        }
        else if(token.equalsIgnoreCase(Token.VIDEO)) { //<video> <inner-text>
            video();
            innerText();
        }
        else if(token.equalsIgnoreCase(Token.LINKB)) { //<link> <inner-text>
            link();
            innerText();
        }
        else if(token.equalsIgnoreCase(Token.NEWLINE)) { //<new-line> <inner-text>
            newline();
            innerText();
        }
        else if(tokenIsText(Compiler.currentToken)) { //TEXT <inner-text>
            parseTree.push(Compiler.currentToken);
            Compiler.lexer.getNextToken();
            innerText();
        }

        //If no cases apply assume empty set and continue on
    }

    //BNF for <variable-define> rule
    //<variable-define> ::= DEFB TEXT EQSIGN TEXT DEFUSEE <variable-define> | E
    public void variableDefine() throws CompilerException {
        if(Compiler.currentToken.equalsIgnoreCase(Token.DEFB)){ //DEFB
            parseTree.push(Compiler.currentToken);
            Compiler.lexer.getNextToken();

            if(tokenIsText(Compiler.currentToken)){ //TEXT
                parseTree.push(Compiler.currentToken);
                Compiler.lexer.getNextToken();

                if(Compiler.currentToken.equalsIgnoreCase(Token.EQSIGN)){ //EQSIGN
                    parseTree.push(Compiler.currentToken);
                    Compiler.lexer.getNextToken();

                    if(tokenIsText(Compiler.currentToken)){ //TEXT
                        parseTree.push(Compiler.currentToken);
                        Compiler.lexer.getNextToken();

                        if(Compiler.currentToken.equalsIgnoreCase(Token.DEFUSEE)){ //DEFUSEE
                            parseTree.push(Compiler.currentToken);
                            Compiler.lexer.getNextToken();

                            variableDefine(); //<variable-define>

                        }
                        else {
                            throw new CompilerException("Unexpected token: " + Compiler.currentToken);
                        }
                    }
                    else {
                        throw new CompilerException("Unexpected token: " + Compiler.currentToken);
                    }
                }
                else {
                    throw new CompilerException("Unexpected token: " + Compiler.currentToken);
                }
            }
            else {
                throw new CompilerException("Unexpected token: " + Compiler.currentToken);
            }
        }
    }

    //BNF for variable use rule
    //<variable-use> ::= USEB TEXT DEFUSEE | E
    public void variableUse() throws CompilerException {
        if(Compiler.currentToken.equalsIgnoreCase(Token.USEB)) { //USEB
            parseTree.push(Compiler.currentToken);
            Compiler.lexer.getNextToken();

            if(tokenIsText(Compiler.currentToken)) { //TEXT
                parseTree.push(Compiler.currentToken);
                Compiler.lexer.getNextToken();

                if(Compiler.currentToken.equalsIgnoreCase(Token.DEFUSEE)) { //DEFUSEE
                    parseTree.push(Compiler.currentToken);
                    Compiler.lexer.getNextToken();
                }
                else {
                    throw new CompilerException("Unexpected token: " + Compiler.currentToken);
                }
            }
            else {
                throw new CompilerException("Unexpected token: " + Compiler.currentToken);
            }
        }

        //If no cases apply assume empty set and continue on
    }

    //BNF for bold rule
    //<bold> ::= BOLD TEXT BOLD | E
    public void bold() throws CompilerException {
        if(Compiler.currentToken.equalsIgnoreCase(Token.BOLD)){ //BOLD
            parseTree.push(Compiler.currentToken);
            Compiler.lexer.getNextToken();

            while(tokenIsText(Compiler.currentToken)) { //TEXT
                parseTree.push(Compiler.currentToken);
                Compiler.lexer.getNextToken();
            }

            if(Compiler.currentToken.equalsIgnoreCase(Token.BOLD)) { //BOLD
                parseTree.push(Compiler.currentToken);
                Compiler.lexer.getNextToken();
            }
            else {
                throw new CompilerException("Unexpected token: " + Compiler.currentToken);
            }
        }

        //If no cases apply assume empty set and continue on
    }

    //BNF for italics rule
    //<italics> ::= ITALICS TEXT ITALICS | E
    public void italics() throws CompilerException {
        if(Compiler.currentToken.equalsIgnoreCase(Token.ITALICS)) { //ITALICS
            parseTree.push(Compiler.currentToken);
            Compiler.lexer.getNextToken();

            while(tokenIsText(Compiler.currentToken)) { //TEXT
                parseTree.push(Compiler.currentToken);
                Compiler.lexer.getNextToken();
            }

            if(Compiler.currentToken.equalsIgnoreCase(Token.ITALICS)) { //ITALICS
                parseTree.push(Compiler.currentToken);
                Compiler.lexer.getNextToken();
            }
            else {
                throw new CompilerException("Unexpected token: " + Compiler.currentToken);
            }
        }
    }

    //BNF for list item rule
    //<list-item> ::= LISTITEMB <inner-item> LISTITEME <list-item> | E
    public void listItem() throws CompilerException {
        if(Compiler.currentToken.equalsIgnoreCase(Token.LISTITEMB)) { //LISTITEMB
            parseTree.push(Compiler.currentToken);
            Compiler.lexer.getNextToken();

            innerItem(); //<inner-item>

            if(Compiler.currentToken.equalsIgnoreCase(Token.LISTITEME)) { //LISTITEME
                parseTree.push(Compiler.currentToken);
                Compiler.lexer.getNextToken();

                listItem(); //<list-item>
            }
            else {
                throw new CompilerException("Unexpected token: " + Compiler.currentToken);
            }
        }

        //If no cases apply assume empty set and continue on
    }

    //BNF for inner item rule
    /*<inner-item> ::= <variable-use> <inner-item> |
                       <bold> <inner-item> |
                       <italics> <inner-item> |
                       <link> <inner-item> |
                       TEXT <inner-item> | E
    */
    public void innerItem() throws CompilerException {
        if(Compiler.currentToken.equalsIgnoreCase(Token.USEB)) {
            variableUse(); //<variable-use>
            innerItem(); //<inner-item>
        }
        else if(Compiler.currentToken.equalsIgnoreCase(Token.BOLD)) {
            bold(); //<bold>
            innerItem(); //<inner-item>
        }
        else if(Compiler.currentToken.equalsIgnoreCase(Token.ITALICS)) {
            italics(); //<italics>
            innerItem(); //<inner-item>
        }
        else if(Compiler.currentToken.equalsIgnoreCase(Token.LINKB)) {
            link(); //<link>
            innerItem(); //inner-item>
        }
        else if(tokenIsText(Compiler.currentToken)) { //TEXT
            parseTree.push(Compiler.currentToken);
            Compiler.lexer.getNextToken();
            innerItem(); //<inner-item>
        }

    }

    //BNF for link rule
    //<link> ::= LINKB TEXT LINKE ADDRESSB TEXT ADDRESSE
    public void link() throws CompilerException {
        if(Compiler.currentToken.equalsIgnoreCase(Token.LINKB)) { //LINKB
            parseTree.push(Compiler.currentToken);
            Compiler.lexer.getNextToken();

            while(tokenIsText(Compiler.currentToken)) { //TEXT
                parseTree.push(Compiler.currentToken);
                Compiler.lexer.getNextToken();
            }

            if(Compiler.currentToken.equalsIgnoreCase(Token.LINKE)) { //LINKE
                parseTree.push(Compiler.currentToken);
                Compiler.lexer.getNextToken();

                if(Compiler.currentToken.equalsIgnoreCase(Token.ADDRESSB)) { //ADDRESSB
                    parseTree.push(Compiler.currentToken);
                    Compiler.lexer.getNextToken();

                    while(tokenIsText(Compiler.currentToken)) { //TEXT
                        parseTree.push(Compiler.currentToken);
                        Compiler.lexer.getNextToken();
                    }

                    if(Compiler.currentToken.equalsIgnoreCase(Token.ADDRESSE)) { //ADDRESSE
                        parseTree.push(Compiler.currentToken);
                        Compiler.lexer.getNextToken();
                    }
                    else {
                        throw new CompilerException("Unexpected token: " + Compiler.currentToken);
                    }
                }
                else {
                    throw new CompilerException("Unexpected token: " + Compiler.currentToken);
                }
            }
            else {
                throw new CompilerException("Unexpected token: " + Compiler.currentToken);
            }
        }
    }

    //BNF for audio rule
    //<audio> ::= AUDIO ADDRESSB TEXT ADDRESSE | E
    public void audio() throws CompilerException {
        if(Compiler.currentToken.equalsIgnoreCase(Token.AUDIO)){ //AUDIO
            parseTree.push(Compiler.currentToken);
            Compiler.lexer.getNextToken();

            if(Compiler.currentToken.equalsIgnoreCase(Token.ADDRESSB)) { //ADDRESSB
                parseTree.push(Compiler.currentToken);
                Compiler.lexer.getNextToken();

                while(tokenIsText(Compiler.currentToken)) { //TEXT
                    parseTree.push(Compiler.currentToken);
                    Compiler.lexer.getNextToken();
                }

                if(Compiler.currentToken.equalsIgnoreCase(Token.ADDRESSE)) { //ADDRESSE
                    parseTree.push(Compiler.currentToken);
                    Compiler.lexer.getNextToken();
                }
                else {
                    throw new CompilerException("Unexpected token: " + Compiler.currentToken);
                }
            }
            else {
                throw new CompilerException("Unexpected token: " + Compiler.currentToken);
            }
        }

        //If no cases apply assume empty set and continue on
    }

    //BNF for video rule
    //<video> ::= VIDEO ADDRESSB TEXT ADDRESSE | E
    public void video() throws CompilerException {
        if(Compiler.currentToken.equalsIgnoreCase(Token.VIDEO)){ //VIDEO
            parseTree.push(Compiler.currentToken);
            Compiler.lexer.getNextToken();

            if(Compiler.currentToken.equalsIgnoreCase(Token.ADDRESSB)) { //ADDRESSB
                parseTree.push(Compiler.currentToken);
                Compiler.lexer.getNextToken();

                while(tokenIsText(Compiler.currentToken)) { //TEXT
                    parseTree.push(Compiler.currentToken);
                    Compiler.lexer.getNextToken();
                }

                if(Compiler.currentToken.equalsIgnoreCase(Token.ADDRESSE)) { //ADDRESSE
                    parseTree.push(Compiler.currentToken);
                    Compiler.lexer.getNextToken();
                } else {
                    throw new CompilerException("Unexpected token: " + Compiler.currentToken);
                }
            } else {
                throw new CompilerException("Unexpected token: " + Compiler.currentToken);
            }
        }

        //If no cases apply assume empty set and continue on
    }


    //BNF for new line rule
    //<new-line> ::= NEWLINE
    public void newline() throws CompilerException {
        if(Compiler.currentToken.equalsIgnoreCase(Token.NEWLINE)){
            parseTree.push(Compiler.currentToken);
            Compiler.lexer.getNextToken();
        }
        else{
            throw new CompilerException("Unexpected token: " + Compiler.currentToken);
        }
    }

}