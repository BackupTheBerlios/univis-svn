package javax.jmi.model;

import javax.jmi.reflect.RefClass;

public interface StructureFieldClass extends RefClass {
    public StructureField createStructureField();
    public StructureField createStructureField(String name, String annotation);
}
