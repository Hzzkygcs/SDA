using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;

namespace HzzGrader.JavaRelated
{
    public enum VisibilityModifier
    {
        PUBLIC,
        PRIVATE,
        PROTECTED,
        DEFAULT
    }

    class VisibilityModifierExtension
    {
        public static VisibilityModifier from_string(string str){
            if (str.Equals("")) return VisibilityModifier.DEFAULT;
            else if (str.Equals("public")) return VisibilityModifier.PUBLIC;
            else if (str.Equals("private")) return VisibilityModifier.PRIVATE;
            else if (str.Equals("protected")) return VisibilityModifier.PROTECTED;
            else throw new InvalidDataException();
        }
    }

    public enum StaticAbstract
    {
        STATIC,
        ABSTRACT,
        NONE
    }

    static class StaticAbstractExtension
    {
        public static StaticAbstract from_string(string str){
            if (str.Equals("static")) return StaticAbstract.STATIC;
            else if (str.Equals("abstract")) return StaticAbstract.ABSTRACT;
            else if (str.Equals("")) return StaticAbstract.NONE;
            else{
                throw new InvalidDataException();
            }
        }
    }


    public abstract class Declaration
    {
        public Match match;
        public VisibilityModifier visibility_modifier;
        public StaticAbstract static_abstract;
        public bool is_synchronized;
        public bool is_final;

        public override string ToString(){
            string temp_final = "";
            string temp_synchronized = "";
            string temp_static_abstract = "";
            string temp_visibility_modifier = "";

            if (is_final) temp_final = "final ";
            if (is_synchronized) temp_synchronized = "synchronized ";
            if (static_abstract == StaticAbstract.STATIC) temp_static_abstract = "static ";
            else if (static_abstract == StaticAbstract.ABSTRACT) temp_static_abstract = "abstract ";
            if (visibility_modifier == VisibilityModifier.PUBLIC) temp_visibility_modifier = "public ";
            else if (visibility_modifier == VisibilityModifier.PRIVATE) temp_visibility_modifier = "private ";
            else if (visibility_modifier == VisibilityModifier.PROTECTED) temp_visibility_modifier = "protected ";

            return String.Format("{0}{1}{2}{3}",
                temp_visibility_modifier, temp_static_abstract, temp_final,
                temp_synchronized);
        }
    }

    public abstract class VariableDeclaration : Declaration
    {
        public ClassDeclaration parent_class;
        public string type; // DOESN'T CATCH ARRAY AND GENERIC []
        public string complete_type; // catch whole type (class name, generic (if any), and square brackets (if any))
        public string type_generic;
        public string name;

        public VariableDeclaration(string name, string type, string complete_type, string type_generic = "",
            VisibilityModifier visibility_modifier = VisibilityModifier.DEFAULT,
            StaticAbstract static_abstract = StaticAbstract.NONE, bool is_synchronized = false,
            bool is_final = false){

            this.is_final = is_final;
            this.is_synchronized = is_synchronized;
            this.static_abstract = static_abstract;
            this.visibility_modifier = visibility_modifier;
            this.type_generic = type_generic;
            this.type = type;
            this.name = name;
        }

        public override string ToString(){
            return String.Format("{0}{1} {2}", base.ToString(), complete_type, name);
        }
    }


    public class AssignedVariableDeclaration : VariableDeclaration
    {
        public string assigned_value;

        public AssignedVariableDeclaration(string assigned_value, string name, string type, string complete_type,
            string type_generic = "", VisibilityModifier visibility_modifier = VisibilityModifier.DEFAULT,
            StaticAbstract static_abstract = StaticAbstract.NONE, bool is_synchronized = false, bool is_final = false) :
            base(name, type, type_generic, complete_type, visibility_modifier,
                static_abstract, is_synchronized, is_final){
            this.assigned_value = assigned_value;
        }

        public override string ToString(){
            return String.Format("{0} = {1};", base.ToString(), assigned_value);
        }
    }


    // complement of AssignedVariableDeclaration
    public class UninitializedVariableDeclaration : VariableDeclaration
    {

        public UninitializedVariableDeclaration(string name, string type, string complete_type,
            string type_generic = "", VisibilityModifier visibility_modifier = VisibilityModifier.DEFAULT,
            StaticAbstract static_abstract = StaticAbstract.NONE, bool is_synchronized = false, bool is_final = false) :
            base(name, type, type_generic, complete_type, visibility_modifier,
                static_abstract, is_synchronized, is_final){
        }

        public override string ToString(){
            return String.Format("{0};", base.ToString());
        }
    }


    public class ClassDeclaration : Declaration
    {
        public string name;
        public string generic;
        public string parent;
        public string parent_generic;
        public string implements;

        public List<VariableDeclaration> variable_declarations = new List<VariableDeclaration>(4);

        public ClassDeclaration(string name, string generic,
            VisibilityModifier visibility_modifier = VisibilityModifier.DEFAULT,
            StaticAbstract static_abstract = StaticAbstract.NONE,
            string parent = "", string parent_generic = "", string implements = ""){
            this.name = name;
            this.generic = generic;
            this.visibility_modifier = visibility_modifier;
            this.static_abstract = static_abstract;
            this.parent = parent;
            this.parent_generic = parent_generic;
            this.implements = implements;
        }

        public override string ToString(){
            return String.Format("{0}{1}", base.ToString(), name);
        }
    }


    public class JavaMiniParser
    {
        /*
         *
         * WE WILL ASSUME THAT THE SOURCE CODE STRING IS SYNTAXLY CORRECT.
         * (finite state automata?)
         * 
         * The algorithm is that, firstly, we must get all strings (and comments) and change it
         * into a super random token. (string tokenizing)
         * 
         * Because, after this, we will looking for:
         *      public class SomeClassName {
         * and then extract its class name information. But if we didn't tokenize the strings first,
         * there's a chance that we will receive a stupid string like this:
         *      "public class SomeClassName {"
         * Which is actually a string, not a class declaration.
         *
         * I give up to handle the multiline string. I hope they don't fill it with a bad value.
         */

        // used to tokenize the strings
        static readonly string _GET_STRING_REGEX = "\"(\\\\\\\\|\\\\\"|[^\"\\n])+\"" + "|" + "\"\"";
        static readonly string _GET_INLINE_COM_REGEX = "\\/\\/[^\\n]*";

        static readonly string _GET_MULTL_COM_REGEX = "\\/\\*([^*]|\\*(?!\\/)|\\n)+?\\*\\/" // nge-match /*ada isinya*/
                                                      + "|" +
                                                      "\\/\\*\\*\\/"; // nge-match  /**/ gaada isinya, bahkan satu spasi pun.

        static readonly Regex GET_CHAR_REGEX = new Regex("'([^'\\\\]|\\\\\\\\|\\\\')'");
        static readonly Regex GET_STRING_REGEX = new Regex(_GET_STRING_REGEX);
        static readonly Regex GET_INLINE_COM_REGEX = new Regex(_GET_INLINE_COM_REGEX);
        static readonly Regex GET_MULT_COM_REGEX = new Regex(_GET_MULTL_COM_REGEX);

        static readonly Regex GET_PUBLIC_CLASS_NAME_REGEX =
            new Regex("public\\s+class\\s+([a-zA-Z0-9_][a-zA-Z0-9_]*)\\s*{");

        
        // excluding primitive types such as int, long, boolean, etc
        public static readonly HashSet<string> _JAVA_KEYWORDS 
            = new HashSet<string>(new []{
                "abstract", "continue", "for", "new", "switch", "assert",  "default", "goto", "super",
                "package", "synchronized", "do", "if", "private", "this", "break",  "while", 
                "implements", "protected", "throw", "else", "import", "public", "throws", "case", "enum", 
                "instanceof", "return", "transient", "catch", "extends", "try", "volatile", 
                "final", "interface", "static", "void", "class", "finally", "strictfp","const", "native"});
 
        public static readonly HashSet<string> _JAVA_PRIMITIVE_TYPES 
            = new HashSet<string>(new []{
                "int", "byte", "short", "long", "float", "double", "boolean", "char",
            });
        
        public static readonly HashSet<string> _JAVA_PRIMITIVE_WRAPPER
            = new HashSet<string>(new []{
                "Integer", "Byte", "Short", "Long", "Float", "Double", "Boolean", "Character",
            });

        
        
        public static readonly string _KEYWORD_MODIFIERS =
            "(?:(public|private|protected)\\s+)?(?:(abstract|static)\\s+)?(?:(final)\\s+)?(?:(synchronized)\\s+)?";
        
        public static readonly string _VARIABLE_DECLARATION_TYPE =
            "(([a-zA-Z0-9_]+)\\s*(<[a-zA-Z0-9_,\\s<>]*>)?(?:\\s*\\[\\s*\\]\\s*)*)\\s+([a-zA-Z0-9_]+)";

        public static readonly Regex GET_CLASS_DECLARATION_REGEX =
            new Regex(_KEYWORD_MODIFIERS +
                      "class\\s+([a-zA-Z0-9_]+)\\s*(<[^;]*>)?\\s*(?:extends\\s+([a-zA-Z0-9_]+)\\s*(<[^;]*>)?)?\\s*(?:implements\\s+([^{]+))?\\s*\\{");

        public static readonly Regex GET_UNINITIALIZED_VARIABLE_DECLARATIONS_REGEX = 
            new Regex(_KEYWORD_MODIFIERS +
                      _VARIABLE_DECLARATION_TYPE + "\\s*;");
        
        //  (?:(public|private|protected)\s+)?(?:(abstract|static)\s+)?(?:(final)\s+)?(?:(synchronized)\s+)?(([a-zA-Z0-9_]+)\s*(<[a-zA-Z0-9_,\s]*>)?(?:\s*\[\s*\]\s*)*)\s+([a-zA-Z0-9_]+)\s*=(?!=)([^;]+);
        public static readonly Regex GET_DECLARED_ASSIGNED_REGEX =
            new Regex(_KEYWORD_MODIFIERS +
                      _VARIABLE_DECLARATION_TYPE + "\\s*=(?!=)([^;]+);");

        public static readonly Regex GET_MAIN_METHOD_REGEX =
            new Regex("public\\s+static\\s+void\\s+main\\s*\\(.+\\)[a-zA-Z0-9_ ]*\\{");

        private static readonly int VISBILITY_GROUP = 1;
        private static readonly int STATIC_ABSTRACT_GROUP = 2;
        private static readonly int FINAL_GROUP = 3;
        private static readonly int SYNCHRONIZED_GROUP = 4;

        private static readonly int CLASS_NAME_GROUP = 5;
        private static readonly int CLASS_GENERIC_GROUP = 6;
        private static readonly int CLASS_PARENT_GROUP = 7;
        private static readonly int CLASS_PARENT_GENERIC_GROUP = 8;
        private static readonly int CLASS_IMPLEMENTS_GROUP = 9;

        private static readonly int VARIABLE_COMPLETE_TYPE_GROUP = 5;
        private static readonly int VARIABLE_TYPE_GROUP = 6;
        private static readonly int GENERIC_GROUP = 7;
        private static readonly int VARIABLE_NAME_GROUP = 8;
        private static readonly int ASSIGNED_VALUE_GROUP = 9;


        private static readonly int TOKEN_SIZE = 32;

        int current_pos = 0;


        private Stack<Tuple<string, string>>
            token_stack = new Stack<Tuple<string, string>>(); // item1: token, item2: the actual content

        public string str;
        public string tokenized_str = null;
        public List<object> tokenized_splitted = new List<object>(10);
        public StringBuilder string_builder;

        public JavaMiniParser(string str){
            this.str = str;
            string_builder = new StringBuilder(3 * str.Length / 2);
        }


        public string get_public_class_name(){
            if (tokenized_str == null)
                throw new NullReferenceException("trying to get class name before parsed");
            return GET_PUBLIC_CLASS_NAME_REGEX.Match(tokenized_str).Groups[1].Value;
        }

        public static ClassDeclaration get_class_declaration_from_match(Match match){
            VisibilityModifier visibility = VisibilityModifierExtension.from_string(
                match.Groups[VISBILITY_GROUP].Value);

            StaticAbstract static_abstract = StaticAbstractExtension.from_string(
                match.Groups[STATIC_ABSTRACT_GROUP].Value);

            return new ClassDeclaration(
                match.Groups[CLASS_NAME_GROUP].Value, match.Groups[CLASS_GENERIC_GROUP].Value,
                VisibilityModifierExtension.from_string(match.Groups[VISBILITY_GROUP].Value),
                StaticAbstractExtension.from_string(match.Groups[STATIC_ABSTRACT_GROUP].Value),
                match.Groups[CLASS_PARENT_GROUP].Value,
                match.Groups[CLASS_PARENT_GENERIC_GROUP].Value, match.Groups[CLASS_IMPLEMENTS_GROUP].Value
            );
        }

        public static AssignedVariableDeclaration get_assigned_variable_declaration_from_match(Match match){
            VisibilityModifier visibility = VisibilityModifierExtension.from_string(
                match.Groups[VISBILITY_GROUP].Value);

            StaticAbstract static_abstract = StaticAbstractExtension.from_string(
                match.Groups[STATIC_ABSTRACT_GROUP].Value);

            return new AssignedVariableDeclaration(match.Groups[ASSIGNED_VALUE_GROUP].Value,
                match.Groups[VARIABLE_NAME_GROUP].Value, match.Groups[VARIABLE_TYPE_GROUP].Value,
                match.Groups[VARIABLE_COMPLETE_TYPE_GROUP].Value, match.Groups[GENERIC_GROUP].Value,
                visibility, static_abstract,
                match.Groups[SYNCHRONIZED_GROUP].Length > 2, match.Groups[FINAL_GROUP].Length > 2);
        }
        
        public static UninitializedVariableDeclaration get_uninitialized_variable_declaration_from_match(Match match){
            VisibilityModifier visibility = VisibilityModifierExtension.from_string(
                match.Groups[VISBILITY_GROUP].Value);

            StaticAbstract static_abstract = StaticAbstractExtension.from_string(
                match.Groups[STATIC_ABSTRACT_GROUP].Value);

            return new UninitializedVariableDeclaration(
                match.Groups[VARIABLE_NAME_GROUP].Value, match.Groups[VARIABLE_TYPE_GROUP].Value,
                match.Groups[VARIABLE_COMPLETE_TYPE_GROUP].Value, match.Groups[GENERIC_GROUP].Value,
                visibility, static_abstract,
                match.Groups[SYNCHRONIZED_GROUP].Length > 2, match.Groups[FINAL_GROUP].Length > 2);
        }

        // must be called after parse()
        public List<ClassDeclaration> get_class_declarations(){
            MatchCollection classes = GET_CLASS_DECLARATION_REGEX.Matches(tokenized_str);
            List<ClassDeclaration> ret = new List<ClassDeclaration>(classes.Count + 1);
            MatchCollection declared_variables = GET_DECLARED_ASSIGNED_REGEX.Matches(tokenized_str);

            if (classes.Count == 0) return ret;
            if (declared_variables.Count == 0){
                for (int i = 0; i < classes.Count; i++){
                    ClassDeclaration temp = get_class_declaration_from_match(classes[i]);
                    temp.match = classes[i];
                    ret.Add(temp);
                }
                return ret;
            }

            // Match prev_class = classes[0];
            ClassDeclaration prev_class_obj = get_class_declaration_from_match(classes[0]);
            int declared_variable_ptr = 0;

            for (int i = 0; i < classes.Count; i++){
                if (!classes[i].Success)
                    continue;

                ClassDeclaration current_class_obj = get_class_declaration_from_match(classes[i]);
                current_class_obj.match = classes[i];
                ret.Add(current_class_obj);

                int this_class_start_pos = classes[i].Index;
                int this_class_end_pos = classes[i].Index + classes[i].Length; // exclusive

                while (declared_variable_ptr < declared_variables.Count){
                    if (!declared_variables[declared_variable_ptr].Success) continue;
                    if (declared_variables[declared_variable_ptr].Index < this_class_start_pos){
                        AssignedVariableDeclaration temp =
                            get_assigned_variable_declaration_from_match(declared_variables[declared_variable_ptr]);
                        temp.parent_class = prev_class_obj;
                        temp.match = declared_variables[declared_variable_ptr];
                        prev_class_obj.variable_declarations.Add(temp);
                    }
                    else break;
                    declared_variable_ptr++;
                }
                // prev_class = classes[i];
                prev_class_obj = current_class_obj;
            }

            while (declared_variable_ptr < declared_variables.Count){
                if (!declared_variables[declared_variable_ptr].Success) continue;

                AssignedVariableDeclaration temp =
                    get_assigned_variable_declaration_from_match(declared_variables[declared_variable_ptr]);
                temp.parent_class = prev_class_obj;
                temp.match = declared_variables[declared_variable_ptr];
                prev_class_obj.variable_declarations.Add(temp);

                declared_variable_ptr++;
            }

            return ret;
        }

        // must be called after parse()
        public List<AssignedVariableDeclaration> get_assigned_variable_declarations(){
            MatchCollection matches = GET_DECLARED_ASSIGNED_REGEX.Matches(tokenized_str);
            List<AssignedVariableDeclaration> ret = new List<AssignedVariableDeclaration>(matches.Count + 2);

            foreach (Match match in matches){
                if (!match.Success)
                    continue;
                if (_JAVA_KEYWORDS.Contains(match.Groups[VARIABLE_TYPE_GROUP].Value))
                    continue;
                var temp = get_assigned_variable_declaration_from_match(match);
                temp.match = match;
                ret.Add(temp);
            }
            return ret;
        }
        
        // must be called after parse()
        public List<UninitializedVariableDeclaration> get_uninitialized_variable_declarations(){
            MatchCollection matches = GET_UNINITIALIZED_VARIABLE_DECLARATIONS_REGEX.Matches(tokenized_str);
            List<UninitializedVariableDeclaration> ret 
                = new List<UninitializedVariableDeclaration>(matches.Count + 2);

            foreach (Match match in matches){
                if (!match.Success)
                    continue;
                if (_JAVA_KEYWORDS.Contains(match.Groups[VARIABLE_TYPE_GROUP].Value))
                    continue;
                var temp = get_uninitialized_variable_declaration_from_match(match);
                temp.match = match;
                ret.Add(temp);
            }
            return ret;
        }
        
        
        

        // must be called after parse()
        public List<AssignedVariableDeclaration> get_assigned_static_variable_declarations(){
            var src = get_assigned_variable_declarations();
            var ret = new List<AssignedVariableDeclaration>(src.Count + 1);

            foreach (var i in src){
                if (i.static_abstract == StaticAbstract.STATIC)
                    ret.Add(i);
            }
            return ret;
        }


        public string parse(){
            current_pos = 0;
            string_builder.Clear();

            while (current_pos < str.Length){
                Match match_chr = GET_CHAR_REGEX.Match(str, current_pos);
                Match match_str = GET_STRING_REGEX.Match(str, current_pos);
                Match match_inline_com = GET_INLINE_COM_REGEX.Match(str, current_pos);
                Match match_mult_com = GET_MULT_COM_REGEX.Match(str, current_pos);

                Match next_match = min_match_index(match_chr, match_str, match_inline_com, match_mult_com);

                if (next_match == null){
                    string_builder.Append(str, current_pos, str.Length - current_pos);
                    break;
                }

                string_builder.Append(str, current_pos, next_match.Index - current_pos);
                if (next_match == match_str || next_match == match_chr){
                    string token = random_string(TOKEN_SIZE);
                    string_builder.Append(token);
                    token_stack.Push(new Tuple<string, string>(token, next_match.Value));
                }
                current_pos = next_match.Index + next_match.Length;
            }

            tokenized_str = string_builder.ToString();
            string_builder.Clear();
            return tokenized_str;
        }


        // memisahkan antara deklarasi class, deklarasi variabel, dengan string source code lainnya,
        // tanpa merubah posisi/urutan tempat mereka berada. Mempermudah mengedit segala deklarasi yg ada 
        // di dalamnya
        public void parse_tokenized_splitted(){
            // must be called after parse()
            List<ClassDeclaration> temp = get_class_declarations();
            List<Declaration> linearized_objects = new List<Declaration>();

            foreach (var class_declaration in temp){
                linearized_objects.Add(class_declaration);
                foreach (var variable_declaration in class_declaration.variable_declarations){
                    linearized_objects.Add(variable_declaration);
                }
            }

            linearized_objects.ForEach(p => Console.Write("{0} ", p));

            linearized_objects.Sort(Comparer<Declaration>.Create(
                (Declaration o1, Declaration o2) => o1.match.Index - o2.match.Index));


            tokenized_splitted.Clear();

            int curr_pos = 0;
            for (int i = 0; i < linearized_objects.Count; i++){
                Declaration current_declaration = linearized_objects[i];

                tokenized_splitted.Add(
                    tokenized_str.Substring(curr_pos, current_declaration.match.Index - curr_pos)
                );
                curr_pos = current_declaration.match.Index;

                tokenized_splitted.Add(
                    current_declaration);
                curr_pos += current_declaration.match.Length;
            }

            tokenized_splitted.Add(tokenized_str.Substring(curr_pos));
        }

        public string unparse(){
            string temp = tokenized_str;
            tokenized_str = null;

            while (token_stack.Count > 0){
                Tuple<string, string> token_and_value = token_stack.Pop();
                temp = temp.Replace(token_and_value.Item1, token_and_value.Item2);
            }

            str = temp;
            return temp;
        }


        public Match min_match_index(params Match[] args){
            Match match = null;
            int min_index = Int32.MaxValue;

            foreach (var arg in args){
                if (arg.Success && arg.Index < min_index){
                    min_index = arg.Index;
                    match = arg;
                }
            }
            return match;
        }

        public bool has_package_statement(){
            return Regex.Match(tokenized_str, @"package\s+[a-zA-Z0-9_.]+\s*;").Success;
        }


        private static Random random = new Random();

        public static string random_string(int length){
            const string chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
            return new string(Enumerable.Repeat(chars, length)
                .Select(s => s[random.Next(s.Length)]).ToArray());
        }
    }
}