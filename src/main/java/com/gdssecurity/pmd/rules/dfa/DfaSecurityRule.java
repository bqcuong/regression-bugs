/*
(C) Copyright  2014-2015 Alberto Fernández <infjaf@gmail.com>
(C) Copyright  2012      Gotham Digital Science, LLC -- All Rights Reserved
 
Unless explicitly acquired and licensed from Licensor under another
license, the contents of this file are subject to the Reciprocal Public
License ("RPL") Version 1.5, or subsequent versions as allowed by the RPL,
and You may not copy or use this file in either source code or executable
form, except in compliance with the terms and conditions of the RPL.

All software distributed under the RPL is provided strictly on an "AS
IS" basis, WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, AND
LICENSOR HEREBY DISCLAIMS ALL SUCH WARRANTIES, INCLUDING WITHOUT
LIMITATION, ANY WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE, QUIET ENJOYMENT, OR NON-INFRINGEMENT. See the RPL for specific
language governing rights and limitations under the RPL. 

This code is licensed under the Reciprocal Public License 1.5 (RPL1.5)
http://www.opensource.org/licenses/rpl1.5

*/


package com.gdssecurity.pmd.rules.dfa;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.dfa.NodeType;
import net.sourceforge.pmd.lang.dfa.VariableAccess;
import net.sourceforge.pmd.lang.dfa.pathfinder.CurrentPath;
import net.sourceforge.pmd.lang.dfa.pathfinder.DAAPathFinder;
import net.sourceforge.pmd.lang.dfa.pathfinder.Executable;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.rule.properties.StringMultiProperty;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;

import org.apache.commons.lang3.StringUtils;
import org.jaxen.JaxenException;

import com.gdssecurity.pmd.Utils;
import com.gdssecurity.pmd.rules.BaseSecurityRule;


public class DfaSecurityRule extends BaseSecurityRule  implements Executable {

	private static final String UNKNOWN_TYPE = "UNKNOWN_TYPE";
	private Map<String, String> cacheReturnTypes = new HashMap<String, String>();
	private Set<String> currentPathTaintedVariables;
    private Set<String> functionParameterTainted = new HashSet<String>();
    private Set<String> fieldTypesTainted = new HashSet<String>();
    
    private Map<String, Class<?>> fieldTypes;
    private Map<String, Class<?>> functionParameterTypes;
    private Set<String> sinks;
    private Set<String> sanitizers;
	private Set<String> sinkAnnotations;
	private Set<String> searchAnnotationsInPackages;
	
	private String[] searchAnnotationsInPackagesArray;
	


    private final PropertyDescriptor<String[]> sinkDescriptor = new StringMultiProperty("sinks", "TODO",
            new String[] { "" }, 1.0f, '|');
    
    private final PropertyDescriptor<String[]> sinkAnnotationsDescriptor = new StringMultiProperty("sinksannotations", "TODO",
            new String[] { com.gdssecurity.pmd.annotations.SQLSink.class.getCanonicalName() }, 1.0f, '|');
    
    private final PropertyDescriptor<String[]> sanitizerDescriptor = new StringMultiProperty("sanitizers", "TODO", 
    		new String[] { "" }, 1.0f, '|');
    
    private final PropertyDescriptor<String[]> annotationsPackagesDescriptor = new StringMultiProperty("search-annotattions-in-packages", "TODO",
    		new String[] { "resources" }, 1.0f, '|');
    

    private RuleContext rc;
    private int methodDataFlowCount;
	
    private List<DataFlowNode> additionalDataFlowNodes = new ArrayList<DataFlowNode>();
	
    private static final int MAX_DATAFLOWS = 1000;
    
    public DfaSecurityRule () {
    	super();
    	this.propertyDescriptors.add(this.sinkDescriptor);
    	this.propertyDescriptors.add(this.sanitizerDescriptor);
    	this.propertyDescriptors.add(this.sinkAnnotationsDescriptor);
    	this.propertyDescriptors.add(this.annotationsPackagesDescriptor);
    	
    }
	
    @Override
    protected void init() {
    	super.init();
    	this.sinks = Utils.arrayAsSet(getProperty(this.sinkDescriptor));
        this.sanitizers = Utils.arrayAsSet(getProperty(this.sanitizerDescriptor));
        this.sinkAnnotations = Utils.arrayAsSet(getProperty(this.sinkAnnotationsDescriptor));
        this.searchAnnotationsInPackages = Utils.arrayAsSet(getProperty(this.annotationsPackagesDescriptor));
        this.searchAnnotationsInPackagesArray = this.searchAnnotationsInPackages.toArray(new String[this.searchAnnotationsInPackages.size()]);
    }


	protected boolean isSanitizerMethod(String type, String method) {
		return this.sanitizers.contains(type+"."+method);
	}
    private boolean isSink(String objectType, String objectMethod) {
        return this.sinks.contains(objectType + "." + objectMethod);
    }
	private boolean isSink(Node node) {
		Class<?> type = getJavaType(node);
		if (type == null) {
			return false;
		}		
		String methodName = UNKNOWN_TYPE;
		if (node instanceof ASTMethodDeclaration ||  node instanceof ASTConstructorDeclaration) {
			Node declarator = node.getFirstChildOfType(ASTMethodDeclarator.class);
			if( declarator == null) {
				return false;
			}
			methodName = declarator.getImage();
			populateCache(type, type.getCanonicalName());
			return isSink(type.getCanonicalName(), methodName);			
			
		}

		return false;
	}
	




	private boolean isTaintedVariable(String variable) {
        return this.currentPathTaintedVariables.contains(variable);
    }
    @Override
    public Object visit(ASTConstructorDeclaration astConstructorDeclaration, Object data) {
    	ASTClassOrInterfaceDeclaration astClass = astConstructorDeclaration.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
        if (astClass == null) {
        	return data;
        }
        this.rc = (RuleContext) data;
        processReturnStatements(astConstructorDeclaration);
        processThrowsStatements(astConstructorDeclaration);
        runFinder(astConstructorDeclaration);
        return data;
        
    }

    @Override
	public Object visit(ASTMethodDeclaration astMethodDeclaration, Object data) {

        ASTClassOrInterfaceDeclaration astClass = astMethodDeclaration.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
        if (astClass == null) {
        	return data;
        }
        
        this.rc = (RuleContext) data;
               
        processReturnStatements(astMethodDeclaration);
        processThrowsStatements(astMethodDeclaration);

        runFinder(astMethodDeclaration);

        super.visit(astMethodDeclaration, data);
        
        return data;
    }

	private void runFinder(Node astMethodDeclaration) {
        DataFlowNode rootDataFlowNode = astMethodDeclaration.getDataFlowNode().getFlow().get(0);
		
        this.methodDataFlowCount = 0;
			
        DAAPathFinder daaPathFinder = new DAAPathFinder(rootDataFlowNode, this, MAX_DATAFLOWS);

        daaPathFinder.run();
		
	}

	private void processReturnStatements (Node node) {
    	processDataFlow(node, "./Block/BlockStatement//TryStatement/CatchStatement//ReturnStatement");
    }
    private void processThrowsStatements (Node node) {
    	processDataFlow(node,  "./Block/BlockStatement//TryStatement/CatchStatement//ThrowStatement");
    }
    private void processDataFlow(Node node, String xpath){
        try { 

			List<? extends Node> statements =  node.findChildNodesWithXPath(xpath);
			if (statements == null || statements.isEmpty()) {
				return;
			}
			int i = 0;
			for (DataFlowNode current: node.getDataFlowNode().getFlow()) {
				for (Node statement: statements) {
					if (current.equals(statement.getDataFlowNode())) {
						DataFlowNode next = node.getDataFlowNode().getFlow().get(i + 1);
						if (!next.isType(NodeType.IF_EXPR)) {
							this.additionalDataFlowNodes.add(next);
						}
					}
				}
				i++;
			}
        	
        }
        catch (JaxenException e) { // NOPMD
        	//
        }
    }

    @Override
	public void execute(CurrentPath currentPath) {

        this.methodDataFlowCount++;
        this.currentPathTaintedVariables = new HashSet<String>();
        this.currentPathTaintedVariables.addAll(this.fieldTypesTainted);
        this.currentPathTaintedVariables.addAll(this.functionParameterTainted);
        
        if (this.methodDataFlowCount < MAX_DATAFLOWS) {
            for (Iterator<DataFlowNode> iterator = currentPath.iterator(); iterator.hasNext();) {
                DataFlowNode iDataFlowNode = iterator.next();
                Node node = iDataFlowNode.getNode();
                if (node instanceof ASTMethodDeclaration || node instanceof ASTConstructorDeclaration) {                	
                    this.currentPathTaintedVariables = new HashSet<String>();
                    if (!isSink(node)) {
                    	addMethodParamsToTaintedVariables(node);
                    }
                    addClassFieldsToTaintedVariables(node);
                    this.currentPathTaintedVariables.addAll(this.fieldTypesTainted);
                    this.currentPathTaintedVariables.addAll(this.functionParameterTainted);
                } else if (node instanceof ASTVariableDeclarator || node instanceof ASTStatementExpression) {
                    handleDataFlowNode(iDataFlowNode);
                } 										
            }


			if (!this.additionalDataFlowNodes.isEmpty()) {
				DataFlowNode additionalRootNode = this.additionalDataFlowNodes.remove(0);
				DAAPathFinder daaPathFinder = new DAAPathFinder(additionalRootNode, this, MAX_DATAFLOWS);
				this.methodDataFlowCount = 0;
				daaPathFinder.run();
			}
			
        } 
    }





	private void addClassFieldsToTaintedVariables(Node node) {
		
		
		this.fieldTypes = new HashMap<String, Class<?>>();
		this.fieldTypesTainted = new HashSet<String>();
		
		ASTClassOrInterfaceBody astBody = node.getFirstParentOfType(ASTClassOrInterfaceBody.class);
		if (astBody == null) {
			return;
		}
		
		List<ASTClassOrInterfaceBodyDeclaration> declarations = astBody.findChildrenOfType(ASTClassOrInterfaceBodyDeclaration.class);
		for (ASTClassOrInterfaceBodyDeclaration declaration: declarations) {
			ASTFieldDeclaration field = declaration.getFirstChildOfType(ASTFieldDeclaration.class);
			if (field != null) {				
				Class<?> type = field.getType();
				ASTVariableDeclarator declarator = field.getFirstChildOfType(ASTVariableDeclarator.class);
				ASTVariableDeclaratorId name1 = declarator.getFirstChildOfType(ASTVariableDeclaratorId.class);
				if (name1 != null) {
					String name = name1.getImage();
					this.fieldTypes.put(name, type);
					if (!field.isFinal() && isUnsafeType(field.getType())) {
						this.fieldTypesTainted.add("this." + name);
					}
				}
			}
		}		
		
	}

	private void addMethodParamsToTaintedVariables(Node node) {
		this.functionParameterTypes = new HashMap<String, Class<?>>();
		this.functionParameterTainted = new HashSet<String>();
		ASTFormalParameters formalParameters = null;
		if (node instanceof ASTMethodDeclaration) {
			ASTMethodDeclarator declarator = node.getFirstChildOfType(ASTMethodDeclarator.class);
			formalParameters = declarator.getFirstChildOfType(ASTFormalParameters.class);
		}
		else if (node instanceof ASTConstructorDeclaration) {
			formalParameters = node.getFirstChildOfType(ASTFormalParameters.class); 
		}
		if (formalParameters == null) {
			return;
		}
		List<ASTFormalParameter> parameters = formalParameters.findChildrenOfType(ASTFormalParameter.class);       
		for (ASTFormalParameter parameter : parameters) {
			ASTType type = parameter.getTypeNode();
			ASTVariableDeclaratorId name1 = parameter.getFirstChildOfType(ASTVariableDeclaratorId.class);						
			String name = name1.getImage();
			if (name != null && type != null) {
				this.functionParameterTypes.put(name, type.getType());
			}
			if (name != null && isUnsafeType(type)){
				this.functionParameterTainted.add(name);
			}
		}
	}


	private void handleDataFlowNode(DataFlowNode iDataFlowNode) {
        for(VariableAccess access : iDataFlowNode.getVariableAccess()) {
        	if (access.isDefinition()){        		
        		String variableName = access.getVariableName();
        		handleVariableDefinition(iDataFlowNode, variableName);
        		return;
        	}
        }
        handleVariableReference(iDataFlowNode);
    }

    private void handleVariableReference(DataFlowNode iDataFlowNode) {

        Node simpleNode = iDataFlowNode.getNode();

        if (isMethodCall(simpleNode)) {
			
            Class<?> type = null;
            String method = "";
			
            Node astMethod = null;
            if (simpleNode.getFirstDescendantOfType(ASTAssignmentOperator.class) == null) {
            	astMethod = simpleNode.getFirstDescendantOfType(ASTPrimaryExpression.class);
            }
            else {
            	astMethod = simpleNode.getFirstDescendantOfType(ASTExpression.class);
            }
            method = getMethod(astMethod);
            type = getJavaType(astMethod);    

            
            if ((type == StringBuilder.class || type == StringBuffer.class) && ("insert".equals(method) || "append".equals(method))){
            	analizeStringBuilderAppend(simpleNode);
            }
            
            if (isSink(type, method)) {
                analyzeSinkMethodArgs(simpleNode);
            }
			
        } 
    }
    
    private boolean isSink(Class<?> type, String methodName) {
    	if (type == null) {
    		return false;
    	}
    	populateCache(type, type.getCanonicalName());
    	return isSink(type.getCanonicalName(), methodName);
    	
	}

	private boolean analizeTypeWithReflectionForAnnotations(Class<?> type) {
		if (this.searchAnnotationsInPackagesArray.length == 0) {
			return false;
		}
		if (type == null || type.getPackage() == null) {
			return false;
		}
		String packageName = type.getPackage().getName();
    	return StringUtils.startsWithAny(packageName, this.searchAnnotationsInPackagesArray);
	}

	private void analizeStringBuilderAppend(Node simpleNode) {
    	ASTName name = simpleNode.getFirstDescendantOfType(ASTName.class);
    	if (name == null) {
    		return;
    	}
    	
    	String varName = getVarName(name);
    	
    	if (this.isTaintedVariable(varName)) {
    		return;
    	}
    	if (isTainted(simpleNode)) {
    		this.currentPathTaintedVariables.add(varName);
    	}
    	
    }

    private void analyzeSinkMethodArgs(Node simpleNode) {
    	if (isAnyArgumentTainted(simpleNode)) {    		
    		addSecurityViolation(this, this.rc, simpleNode, getMessage(), "");
    	}

    }

    private boolean isAnyArgumentTainted (Node simpleNode) {
        ASTArgumentList argListNode = simpleNode.getFirstDescendantOfType(ASTArgumentList.class); 
        if (argListNode != null) {        	
	        for(int i = 0; i < argListNode.jjtGetNumChildren(); i++) {
	        	Node argument = argListNode.jjtGetChild(i);	        	
	        	if (isTainted(argument)){
	        		return true;
	        	}
	        }
        }
        return false;
    }


    private boolean isMethodCall(Node node) {
        ASTArguments arguments = node.getFirstDescendantOfType(ASTArguments.class);
        return arguments != null;
    }

	private void handleVariableDefinition(DataFlowNode iDataFlowNode, String variable) {
		Node simpleNode = iDataFlowNode.getNode();
		Class<?> clazz = String.class;
		
		
		Node primaryExpression = simpleNode.jjtGetChild(0);
		if (primaryExpression instanceof ASTPrimaryExpression) {
			Node primaryPrefix = primaryExpression.jjtGetChild(0);
			if (primaryPrefix instanceof ASTPrimaryPrefix) {
				clazz = ((ASTPrimaryPrefix) primaryPrefix).getType();
			}
		}
		if (primaryExpression instanceof ASTVariableDeclaratorId && simpleNode.jjtGetNumChildren() > 1) {
			Node initializer = simpleNode.jjtGetChild(1);
			if (initializer instanceof ASTVariableInitializer) {
				clazz = ((ASTVariableDeclaratorId)primaryExpression).getType();
			}
		}

		
		
				
		if (isTainted(simpleNode) && isUnsafeType(clazz)) {
			this.currentPathTaintedVariables.add(variable);
		}
	}
    
    private boolean isTainted(Node node2) {
    	List<ASTPrimaryExpression> primaryExpressions = getExp(node2);
    	for (ASTPrimaryExpression node: primaryExpressions) {
    		if (node.jjtGetParent() instanceof ASTConditionalExpression && node.jjtGetParent().jjtGetChild(0) == node){
    			continue;
    		}
    		if (isMethodCall(node)) {
                String method = getMethod(node);
                String type = getType(node);
                if (isSanitizerMethod(type, method)) {
                	continue;
                }
                else if (isSink(type, method)) {
                    analyzeSinkMethodArgs(node);
                }         
                else if (isSafeType(getReturnType(node, type, method))){
                	continue;
                }
                else if (isSource(type, method) || isUsedOverTaintedVariable(node) || isAnyArgumentTainted(node)) {
                    return true;
                }

            } else if (node.hasDescendantOfType(ASTName.class)){
                List<ASTName> astNames = node.findDescendantsOfType(ASTName.class);
                if (analyzeVariable(astNames)){
                	return true;
                }
            }
            else if (isUsedOverTaintedVariable(node)){
            	return true;
            }
    		boolean childsTainted = isTainted(node);
    		if (childsTainted) {
    			return true;
    		}
    	}
    	return false;
    	
    }
    
    private boolean isUsedOverTaintedVariable(Node node) {
    	ASTPrimaryPrefix prefix = node.getFirstChildOfType(ASTPrimaryPrefix.class);
    	ASTPrimarySuffix suffix = node.getFirstChildOfType(ASTPrimarySuffix.class);
    	if ((prefix == null || prefix.getImage() == null) && suffix != null && suffix.getImage() != null){
    		String fieldName = suffix.getImage();
    		if (this.currentPathTaintedVariables.contains("this." + fieldName)){
    			return true;
    		}
    	}
		if (prefix != null) {
			ASTName astName = prefix.getFirstChildOfType(ASTName.class);
			if (astName != null) {
				String varName = getVarName(astName);
				return isTaintedVariable(varName);
			}
		}
    	return false;
    }
    
    
    private String getVarName(ASTName name) {
    	String varName = name.getImage();
    	if (varName.startsWith("this.")) {
    		varName = StringUtils.removeStart(varName, "this.");
    	}
    	else if (varName.contains(".")){
    		varName = StringUtils.split(varName, ".")[0];
    	}
		if (varName.indexOf('.') != -1) {
			varName = varName.substring(varName.indexOf('.') + 1);
		}
		if (isField(name)) {
			varName = "this." + varName;
		}
		return varName;
    }
    
    
    private String getReturnType(Class<?> clazz, String realType, String methodName) {
    	if (!this.cacheReturnTypes.containsKey(realType)) {
    		 populateCache(clazz, realType);
    	}
    	String retVal = this.cacheReturnTypes.get(realType + "." + methodName);
    	if (StringUtils.isBlank(retVal)){
    		return UNKNOWN_TYPE;
    	}
    	return retVal;
    }

    
    private void populateCache(Class<?> clz, String realType) {
    	if (this.cacheReturnTypes.containsKey(realType)) {
    		return;
    	}
    	this.cacheReturnTypes.put(realType, realType);
    	Class<?> clazz = clz;
    	try {
	    	if (clazz == null) {
	    		clazz = Class.forName(realType, false, this.getClass().getClassLoader());
	    	}
	    	if (clazz != null) {
		    	for(Method method: clazz.getMethods()) {
		    		Class<?> returnType = method.getReturnType();
		    		String methodName = method.getName();
		    		String key = clazz.getCanonicalName() + "." + methodName;
		    		if (returnType != null && !"void".equals(returnType.getCanonicalName())){
		    			String old = this.cacheReturnTypes.get(key);
		    			if (old == null || StringUtils.equals(old, returnType.getCanonicalName())){
		    				this.cacheReturnTypes.put(key, returnType.getCanonicalName());
		    			}
						// else {
						// // various return types for same method
						// cacheReturnTypes.put(key, UNKNOWN_TYPE);
						// }
		    			
		    		}
	    			if (analizeTypeWithReflectionForAnnotations(clazz)) {
	    				Annotation[] annotations = method.getAnnotations();
	    		    	for (Annotation annotation: annotations) {
	    		    		if (this.sinkAnnotations.contains(annotation.annotationType().getCanonicalName())){
	    		    			this.sinks.add(clazz.getCanonicalName() + "." + method.getName());
	    		    		}
	    		    	}
	    	    	}

		    	}
	    	}
		} catch (NoClassDefFoundError err) { //NOPMD
		} catch (ExceptionInInitializerError err) { //NOPMD
		} catch (ClassNotFoundException e) { //NOPMD
		}
	}

	private String getReturnType(ASTPrimaryExpression node, String type, String methodName) {
    	String realType = type;

		Class<?> clazz = null;
		if (StringUtils.isBlank(realType) || UNKNOWN_TYPE.equals(realType)) {
			ASTClassOrInterfaceDeclaration type2 = node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
			if (type2 != null && type2.getType() != null) {
				clazz = type2.getType();
				realType = clazz.getCanonicalName();
			}
		}
		if (StringUtils.isBlank(realType) || UNKNOWN_TYPE.equals(realType)) {
			return UNKNOWN_TYPE;
		}
		return getReturnType(clazz, realType, methodName);


	}

	private List<ASTPrimaryExpression> getExp(Node node2) {
    	List<ASTPrimaryExpression> expressions = new ArrayList<ASTPrimaryExpression>();
    	for (int i=0; i < node2.jjtGetNumChildren(); i++) {
    		Node child = node2.jjtGetChild(i);
    		if (child instanceof ASTPrimaryExpression) {
    			expressions.add((ASTPrimaryExpression) child);
    		}
    		else {
    			expressions.addAll(getExp(child));
    		}
    	}
    	
		return expressions;
	}


	private String getMethod(Node node) {
        String method = getFullMethodName(node); 
        if (method.indexOf('.') != -1) {
            method = method.substring(method.indexOf('.') + 1);
        }
        return method;
    }
    
    private String getFullMethodName(Node node) {
    	ASTClassOrInterfaceType astClass = node.getFirstChildOfType(ASTClassOrInterfaceType.class);
        if (astClass != null) {
            return astClass.getImage();
        }
		ASTPrimaryPrefix prefix = node.getFirstChildOfType(ASTPrimaryPrefix.class);
		
		if (prefix != null) {
			ASTName astName = prefix.getFirstChildOfType(ASTName.class);
			if (astName != null && astName.getImage() != null) {
				return astName.getImage();
			}
		}
		if (prefix == null) {
			ASTName astName = node.getFirstDescendantOfType(ASTName.class);
			if (astName != null && astName.getImage() != null) {
				return astName.getImage();
			}
		}
		StringBuilder mName = new StringBuilder();
		List<ASTPrimarySuffix> suffixes = node.findChildrenOfType(ASTPrimarySuffix.class);
		for (ASTPrimarySuffix suffix : suffixes) {
			if (!suffix.hasDescendantOfType(ASTArguments.class) && suffix.getImage() != null) {
				if (mName.length() > 0) {
					mName.append(".");
				}
				mName.append(suffix.getImage());
			}
		}
		return mName.toString();		
    }
    
    private String getType(Node node) {
		
        String cannonicalName = UNKNOWN_TYPE;
        Class<?> type = null;
		
        try {
        	type = getJavaType(node);
			if (type != null) {
				cannonicalName = type.getCanonicalName();
			}
			return cannonicalName;
        } catch (Exception ex1) {    		
        	return cannonicalName;
        }
		
    }
    
    
    private Class<?> getJavaType(Node node) {
    	try {
	    	Class<?> type = null;
	        if (node instanceof ASTExpression) {				
	            type = node.getFirstChildOfType(ASTPrimaryExpression.class).getFirstChildOfType(ASTName.class).getType();
	        } else if (node instanceof ASTPrimaryExpression) {
	        	ASTClassOrInterfaceType astClass = node.getFirstChildOfType(ASTClassOrInterfaceType.class);
	            if (astClass != null) {					
	                type = astClass.getType();
	            } else {	
	            	ASTPrimaryPrefix prefix = node.getFirstChildOfType(ASTPrimaryPrefix.class);
	            	ASTName astName = prefix.getFirstChildOfType(ASTName.class);        	
	            	if (astName != null) {
	            		type = astName.getType();
	            		if (type == null) {
	            			String parameterName = astName.getImage();
	            			if (parameterName.indexOf('.') > 0) {
	            				parameterName = parameterName.substring(0, parameterName.indexOf('.'));
	            			}
	            			type = this.functionParameterTypes.get(parameterName);
	            		}
	            	}
	            	else {
	            		ASTPrimarySuffix suffix = node.getFirstChildOfType(ASTPrimarySuffix.class);
	            		type = this.fieldTypes.get(suffix.getImage());
	            	}
	            }
	        } else if (node instanceof ASTName) {
	            type = ((ASTName) node).getType();
	        }
	        else if (node instanceof ASTMethodDeclaration || node instanceof ASTConstructorDeclaration) {
	        	ASTClassOrInterfaceDeclaration type2 = node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
	        	if (type2 != null) {
					type = type2.getType();
				}
	        }
	        return type;
    	} catch (Exception e) {
    		return null;
    	}
	}

	private boolean analyzeVariable(List<ASTName> listOfAstNames) {
		for (ASTName name : listOfAstNames) {
			String var = getVarName(name);			

			if (isTaintedVariable(var) || isSource(getType(name), var)) {
				return true;
			}
		}
		return false;
    }

	private boolean isField(ASTName name) {
		NameDeclaration declaration = name.getNameDeclaration();
		return declaration != null && !declaration.getNode().getParentsOfType(ASTFieldDeclaration.class).isEmpty();
	}

}
