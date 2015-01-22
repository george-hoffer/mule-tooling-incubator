package org.mule.tooling.devkit.treeview.model;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.swt.graphics.Image;
import org.mule.tooling.devkit.DevkitImages;

public class ModuleTransformer extends ModuleMethod {

    public ModuleTransformer(NodeItem parent, ICompilationUnit cu, ASTNode node) {
        super(parent, cu, node);
    }

    @Override
    public Image getImage() {

        return DevkitImages.getManagedImage("", "transformer.gif");
    }
}
