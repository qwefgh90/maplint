package org.javacs.lsp.custom;

import com.sun.source.tree.Tree;
import org.javacs.lsp.Location;
import org.javacs.lsp.SymbolInformation;

public class ExtendedSymbolInformation extends SymbolInformation {
    public Tree type;
    public Location typeLocation;
}
