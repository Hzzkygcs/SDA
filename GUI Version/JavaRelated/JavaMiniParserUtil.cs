using System.Collections.Generic;
using System.Diagnostics;

namespace HzzGrader.JavaRelated
{
    public static class JavaMiniParserUtil
    {
        public static List<VariableDeclaration> 
                get_static_assigned_var_dec_not_in_public_class(JavaMiniParser java_mini_parser){
            
            Debug.Assert(java_mini_parser.tokenized_str != null);

            List<ClassDeclaration> class_declarations = java_mini_parser.get_class_declarations();
            List<VariableDeclaration> ret = new List<VariableDeclaration>();
            
            foreach (var class_declaration in class_declarations){
                if (class_declaration.visibility_modifier == VisibilityModifier.PUBLIC)
                    continue;
                
                foreach (var variable_declaration in class_declaration.variable_declarations){
                    if (variable_declaration.static_abstract == StaticAbstract.STATIC)
                        ret.Add(variable_declaration);
                }
            }
            return ret;
        }
        
        
    }
}