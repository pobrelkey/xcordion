package org.hiro.psi;

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiImportStatement;

import java.util.List;

public interface PsiClassBuilder<T extends PsiClassBuilder, M> extends Builder<M> {

    PsiClassBuilder<T, M> withDirectory(PsiDirectory directory);

    PsiClassBuilder<T, M> withName(String className);

    PsiClassBuilder<T, M> withImports(List<PsiImportStatement> importStatements);

    M build();
}
