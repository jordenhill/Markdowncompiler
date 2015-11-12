package jordenhill.project1.implementation;

import java.io.*;
import java.util.*;
import java.util.List;

//A Semantic Analyzer
//Gets the parse tree from teh syntax analyzer and checks for any semantic errors in written markdown text.
//If the text checks out is it translated to html and written to an html file.
public class SemanticAnalyzer {
    private final List<String> Markdowntags;
    private final List<String> HTMLtags;
    private final List<String> beginningLexemes;
    private final List<String> endingLexemes;
    private final List<String> repeatedTags;
    private final Stack<String> htmlStack;
    private final Stack<String> buffer;
    private final Stack<String> tokenStack;
    private boolean withinHeadTag = false;
    private boolean withinBoldTag = false;
    private boolean withinItalicTag = false;

    //Constructor
    public SemanticAnalyzer(Stack<String> tokenStack) throws CompilerException {
        Markdowntags = Token.getRelevantHTMLTags();
        HTMLtags = Arrays.asList(HTML.tags);
        this.tokenStack = tokenStack;
        htmlStack = new Stack<>();
        buffer = new Stack<>();
        beginningLexemes = Token.getBeginningLexemes();
        endingLexemes = Arrays.asList(Token.DOCE,Token.TITLEE,Token.PARAE,Token.LISTITEME,Token.ADDRESSE);
        repeatedTags = Arrays.asList(Token.BOLD, Token.ITALICS, Token.HEAD);
        while (!this.tokenStack.isEmpty()) {
            analyze(this.tokenStack.pop());
        }
    }

    //Translate tags and text to html
    private String translate(String string){
        if(Markdowntags.contains(string.toUpperCase())){
            return HTMLtags.get(Markdowntags.indexOf(string.toUpperCase()));
        }
        else if(Token.isMarkdownTag(string.toUpperCase())){
            return string;
        }
        return string + " ";
    }

    //Translate a link to html
    private String translateLink(String address){
        String urlLable = "";
        boolean finished = false;

        while(!finished){
            String text = htmlStack.pop();

            if(text.equalsIgnoreCase(Token.LINKB)){
                finished = true;
            }
            else if (htmlStack.peek().equalsIgnoreCase(Token.LINKB)) {
                urlLable = text + " " + urlLable;
            }
            else {
                urlLable = text + urlLable;
            }
        }
        return "<a href=\"" + address.trim() + "\">" + urlLable + "</a>";
    }

    //translate a web address to html
    private String translateAddress(String address){
        String pop = htmlStack.pop();

        switch(pop){
            case Token.AUDIO:
                return HTML.AUDIOBEGIN + "<source src=\""+address.trim()+"\">" + HTML.AUDIOEND;
            case Token.VIDEO:
                return "<iframe src=\""+address.trim()+"\"/>";
            case Token.LINKE:
                return translateLink(address);

        }
        return null;
    }

    //Define variables
    private void definedVariable(String token){
        String result = token;
        result = result + " " +tokenStack.pop();
        tokenStack.pop();
        result = result + " " + tokenStack.pop();
        tokenStack.pop();
        htmlStack.push(result);
    }

    //Use a variable
    private void useVariable(String token) throws CompilerException{
        String name = tokenStack.pop();
        tokenStack.pop();
        Stack<String> tempStack = new Stack();

        boolean didFind = false;

        while(!htmlStack.isEmpty() && !didFind){
            String pop = htmlStack.pop();
            tempStack.push(pop);
            if(pop.split(" ")[0].equalsIgnoreCase(Token.DEFB) && pop.split(" ")[1].equals(name)){
                while(!tempStack.isEmpty()){
                    htmlStack.push(tempStack.pop());
                }
                htmlStack.push(pop.split(" ")[2]);
                didFind = true;
            }
        }
        if(!didFind){
            throw new CompilerException("Unresolved variable: "+name);
        }
    }

    //Check if the token is within the corresponding list of tokens
    private boolean tokenInList(List<String> list, String token){
        for(String tag : list){
            if(token.equalsIgnoreCase(tag)){
                return true;
            }
        }
        return false;
    }

    //Translate head text to html (^text^ -> <head>text</head>
    private void translateHead(String token) throws CompilerException {
        if(withinHeadTag){
            withinHeadTag =false;
            Stack<String> tempStack = new Stack<>();
            boolean found = false;
            while(!htmlStack.isEmpty() && !found){
                String pop = htmlStack.pop();
                tempStack.push(pop);
                if(pop.equalsIgnoreCase(Token.HEAD)){
                    tempStack.pop();
                    htmlStack.push(HTML.HEAD);
                    while(!tempStack.isEmpty()){
                        htmlStack.push(tempStack.pop());
                    }
                    htmlStack.push(HTML.HEADEND);
                    found = true;
                }
            }

            if(!found){ //Head tag (^) not found, possibly another text tag
                throw new CompilerException("Incorrect Delimeter: " + token);
            }
        }
        else {
            withinHeadTag = true;
            htmlStack.push(token);
        }
    }

    //Translate bold text to html (**text** -> <b>text</b>)
    private void translateBold(String token) throws CompilerException{
        if(withinBoldTag){
            withinBoldTag =false;
            Stack<String> tempStack = new Stack<>();
            boolean found = false;

            while(!htmlStack.isEmpty() && !found){
                String pop = htmlStack.pop();
                tempStack.push(pop);

                if(pop.equalsIgnoreCase(Token.BOLD)){
                    tempStack.pop();
                    htmlStack.push(HTML.BOLD);

                    while(!tempStack.isEmpty()){
                        htmlStack.push(tempStack.pop());
                    }

                    htmlStack.push(HTML.BOLDE);
                    found = true;
                }
            }

            if(!found){ //Bold tag (**) not found, possibly another text tag
                throw new CompilerException("Incorrect Delimeter: " + token);
            }
        }
        else {
            withinBoldTag = true;
            htmlStack.push(token);
        }
    }

    //Translate italic text to html (*text* -> <i>text></i>)
    private void translateItalic(String token) throws CompilerException{
        if(withinItalicTag){
            withinItalicTag =false;
            Stack<String> tempStack = new Stack<>();
            boolean found = false;

            while(!htmlStack.isEmpty() && !found){
                String pop = htmlStack.pop();
                tempStack.push(pop);

                if(pop.equalsIgnoreCase(Token.ITALICS)){
                    tempStack.pop();
                    htmlStack.push(HTML.ITALIC);

                    while(!tempStack.isEmpty()){
                        htmlStack.push(tempStack.pop());
                    }

                    htmlStack.push(HTML.ITALICE);
                    found = true;
                }
            }

            if(!found){
                throw new CompilerException("Mismatched Delimeter: " + token);
            }
        }
        else {
            withinItalicTag = true;
            htmlStack.push(token);
        }
    }

    //Analyze the tokens and translate them to HTML
    public void analyze(String token) throws CompilerException{
        if(token.equalsIgnoreCase(Token.DEFB)){
            definedVariable(token);
        }
        else if(token.equalsIgnoreCase(Token.USEB)){
            useVariable(token);
        }
        else if(tokenInList(repeatedTags,token)){
            if(token.equalsIgnoreCase(Token.HEAD)){ //Is a head tag
                translateHead(token);
            }
            else if(token.equalsIgnoreCase(Token.BOLD)){ //Is a bold tag
                translateBold(token);
            }
            else if(token.equalsIgnoreCase(Token.ITALICS)){ //Is an italic tag
                translateItalic(token);
            }
        }
        else if(tokenInList(beginningLexemes,token)){
            htmlStack.push(token);
        }
        else if(tokenInList(endingLexemes,token)){
            int endIndex = endingLexemes.indexOf(token.toUpperCase());
            boolean textBuildComplete = false;
            buffer.push(translate(token));

            while(!textBuildComplete){
                String currentToken = htmlStack.pop();
                String checkDefB = currentToken.split(" ")[0];
                if(checkDefB.equalsIgnoreCase(Token.DEFB)){ //ignore

                }
                else if(currentToken.equalsIgnoreCase(Token.ADDRESSE)){
                    String address = "";
                    boolean doneBuildingAddress = false;

                    while(!doneBuildingAddress){
                        String nextToken = htmlStack.pop();
                        if(nextToken.equalsIgnoreCase(Token.ADDRESSB)){
                            doneBuildingAddress = true;
                        }
                        else {
                            address = nextToken + address;
                        }
                    }
                    htmlStack.push(translateAddress(address));

                }
                else if(currentToken.equalsIgnoreCase(beginningLexemes.get(endIndex))) {
                    htmlStack.push(translate(currentToken));
                    while(!buffer.isEmpty()){
                        htmlStack.push(buffer.pop());
                    }
                    textBuildComplete = true;
                }
                else {
                    buffer.push(translate(currentToken));
                }
            }
        } else {
            htmlStack.push(token);
        }
    }

    //Write HTML file
    public void write(String name) throws CompilerException {
        PrintWriter writer;
        try {
            writer = new PrintWriter(name, "UTF-8");
            while(!htmlStack.isEmpty()){
                String htmlText = htmlStack.pop();
                buffer.push(htmlText);
            }
            while(!buffer.isEmpty()){
                writer.print(buffer.pop());
            }
            writer.close();
        }
        catch (FileNotFoundException e) {
            throw new CompilerException(name + " does not exist");
        }
        catch (UnsupportedEncodingException e) {
            throw new CompilerException("Unsupported Encoding");
        }
    }
}
