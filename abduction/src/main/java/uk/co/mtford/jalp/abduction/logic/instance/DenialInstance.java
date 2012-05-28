/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.mtford.jalp.abduction.logic.instance;

import org.apache.log4j.Logger;
import uk.co.mtford.jalp.abduction.AbductiveFramework;
import uk.co.mtford.jalp.abduction.rules.RuleNode;

import java.util.*;

/**
 * @author mtford
 */
public class DenialInstance implements IInferableInstance, IFirstOrderLogicInstance {

    private static final Logger LOGGER = Logger.getLogger(DenialInstance.class);

    private List<IInferableInstance> body;
    private Set<VariableInstance> universalVariables;

    public DenialInstance(List<IInferableInstance> body,
                          List<VariableInstance> universalVariables) {
        this.body = body;
        this.universalVariables = new HashSet<VariableInstance>(universalVariables);
    }

    public DenialInstance(List<IInferableInstance> body
    ) {
        this.body = body;
        this.universalVariables = new HashSet<VariableInstance>();
    }

    public DenialInstance(List<VariableInstance> universalVariables, IInferableInstance... body) {
        this.universalVariables = new HashSet<VariableInstance>(universalVariables);
        this.body = new LinkedList<IInferableInstance>(Arrays.asList(body));
    }

    public DenialInstance(IInferableInstance... body) {
        universalVariables = new HashSet<VariableInstance>();
        this.body = new LinkedList<IInferableInstance>(Arrays.asList(body));
    }

    public DenialInstance() {
        body = new LinkedList<IInferableInstance>();
        universalVariables = new HashSet<VariableInstance>();
    }

    public List<IInferableInstance> getBody() {
        return body;
    }

    public void setBody(List<IInferableInstance> body) {
        this.body = body;
    }

    public List<VariableInstance> getUniversalVariables() {
        return new LinkedList<VariableInstance>(universalVariables);
    }

    public void setUniversalVariables(List<VariableInstance> universalVariables) {
        this.universalVariables = new HashSet<VariableInstance>(universalVariables);
    }

    @Override
    public String toString() {
        String rep = "ic";
        if (!universalVariables.isEmpty()) {
           rep+="(";
           for (VariableInstance v:universalVariables) rep+=v+",";
           rep = rep.substring(0,rep.length()-1);
           rep+=")";
       }
        rep += " :- ";
        String bodyRep = body.toString();
        bodyRep = bodyRep.substring(1, bodyRep.length() - 1);
        rep += bodyRep + ".";
        return rep;
    }

    public DenialInstance shallowClone() {
        return new DenialInstance(new LinkedList<IInferableInstance>(body),
                new LinkedList<VariableInstance>(universalVariables));
    }

    @Override
    public RuleNode getPositiveRootRuleNode(AbductiveFramework abductiveFramework, List<IInferableInstance> goals) {
        LinkedList<DenialInstance> nestedDenialList = new LinkedList<DenialInstance>();
        if (this.getBody().size()==0) {
            return new FalseInstance().getPositiveRootRuleNode(abductiveFramework,goals);
        }
        DenialInstance shallowClone = this.shallowClone();
        nestedDenialList.add(0,shallowClone);
        return shallowClone.getBody().remove(0).getNegativeRootRuleNode(abductiveFramework, nestedDenialList, goals);
    }

    @Override
    public RuleNode getNegativeRootRuleNode(AbductiveFramework abductiveFramework, List<DenialInstance> nestedDenialList, List<IInferableInstance> goals) {
        if (this.getBody().size()==0) {
            return new FalseInstance().getNegativeRootRuleNode(abductiveFramework,nestedDenialList,goals);
        }
        DenialInstance shallowClone = this.shallowClone();
        nestedDenialList.add(0, shallowClone);
        return shallowClone.getBody().remove(0).getNegativeRootRuleNode(abductiveFramework, nestedDenialList, goals);
    }

    @Override
    public IFirstOrderLogicInstance performSubstitutions(Map<VariableInstance, IUnifiableAtomInstance> substitutions) {
        // Substitute universal variables.
        LinkedList<IInferableInstance> newBody = new LinkedList<IInferableInstance>();

        for (VariableInstance key:substitutions.keySet()) {
            if (universalVariables.contains(key)) {
                universalVariables.remove(key);
            }
        }

        for (IInferableInstance inferable : body) {
            newBody.add((IInferableInstance) inferable.performSubstitutions(substitutions));
        }
        body = newBody;
        return this;
    }

    @Override
    public IFirstOrderLogicInstance deepClone(Map<VariableInstance, IUnifiableAtomInstance> substitutions) {
        // Substitute universal variables.
        LinkedList<IInferableInstance> newBody = new LinkedList<IInferableInstance>();
        LinkedList<VariableInstance> newUniversalVariables = new LinkedList<VariableInstance>();

        for (VariableInstance v:universalVariables) {
            v.deepClone(substitutions);
            IUnifiableAtomInstance newV = v;
            while (substitutions.containsKey(newV)) newV = substitutions.get(newV);
            if (newV instanceof VariableInstance) newUniversalVariables.add((VariableInstance)newV);
        }

        for (IInferableInstance inferable : body) {
            newBody.add((IInferableInstance) inferable.deepClone(substitutions));
        }

        return new DenialInstance(newBody, newUniversalVariables);
    }


    @Override
    public Set<VariableInstance> getVariables() {
        HashSet<VariableInstance> variables = new HashSet<VariableInstance>();
        for (IInferableInstance inferable : body) {
            variables.addAll(inferable.getVariables());
        }
        return variables;
    }
}
