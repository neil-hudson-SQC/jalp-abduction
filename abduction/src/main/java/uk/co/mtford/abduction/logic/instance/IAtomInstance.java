/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.mtford.abduction.logic.instance;

import java.util.List;
import uk.co.mtford.abduction.asystem.EqualityInstance;

/**
 *
 * @author mtford
 */
public interface IAtomInstance extends ILogicInstance {
    public boolean equalitySolveAssign(IAtomInstance other);
    /** 
     * 
     * @param other
     * @return Empty list if can't be broken down into further equalities
     *         otherwise list of new equalities.
     */
    public List<EqualityInstance> equalitySolve(IAtomInstance other);
}
