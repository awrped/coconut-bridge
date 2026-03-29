package dev.coconut.javahooks;

import dev.coconut.av.Coconut;
import dev.coconut.javahooks.impl.java.lang.ProcessBuilderTransformer;
import dev.coconut.javahooks.impl.java.net.URLClassLoaderTransformer;
import dev.coconut.javahooks.impl.java.net.URLTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JavaHooks {


    private static List<IClassTransformer> transformers = Arrays.asList(
            new ProcessBuilderTransformer(),
            new URLClassLoaderTransformer(),
            new URLTransformer()
    );

    public static byte[] process(String className, byte[] classData) {

        final ClassReader classReader = new ClassReader(classData);
        final ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        Coconut.getInstance().scan(classNode);

        for(IClassTransformer transformer : transformers) {
            if(transformer.getClassName().replaceAll("\\.", "/").equals(className)) {
                transformer.process(classNode);
            }
        }

        final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);

        return classWriter.toByteArray();
    }

}
