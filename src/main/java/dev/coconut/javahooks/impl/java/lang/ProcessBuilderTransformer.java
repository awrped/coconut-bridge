package dev.coconut.javahooks.impl.java.lang;

import dev.coconut.javahooks.HookStatus;
import dev.coconut.javahooks.IClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ProcessBuilderTransformer extends IClassTransformer {
    public ProcessBuilderTransformer() {
        super("java/lang/ProcessBuilder");
    }

    public static native HookStatus nativeHook(ProcessBuilder builder);

    @Override
    public void process(ClassNode classNode) {

        /* This is AI
        * - 0x150 (very wise words)
        */

        for(MethodNode mn : classNode.methods) {
            if(mn.name.equals("start")) {
                InsnList hook = new InsnList();
                hook.add(new VarInsnNode(Opcodes.ALOAD, 0));  // push ProcessBuilder onto stack
                hook.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "dev/coconut/javahooks/impl/java/lang/ProcessBuilderTransformer",
                        "nativeHook",
                        "(Ljava/lang/ProcessBuilder;)Ldev/coconut/javahooks/HookStatus;",
                        false));

                hook.add(new FieldInsnNode(Opcodes.GETSTATIC,
                        "dev/coconut/javahooks/HookStatus",
                        "CANCEL",
                        "Ldev/coconut/javahooks/HookStatus;"));

                LabelNode passLabel = new LabelNode();
                hook.add(new JumpInsnNode(Opcodes.IF_ACMPNE, passLabel)); // here we jump to pass if it's not cancel

                // if it's cancel, we throw an exception
                hook.add(new TypeInsnNode(Opcodes.NEW, "java/lang/RuntimeException"));
                hook.add(new InsnNode(Opcodes.DUP));
                hook.add(new LdcInsnNode("coconut: prevented call to ProcessBuilder.start()"));
                hook.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
                        "java/lang/RuntimeException",
                        "<init>",
                        "(Ljava/lang/String;)V",
                        false));
                hook.add(new InsnNode(Opcodes.ATHROW));

                hook.add(passLabel); // our pass label

                mn.instructions.insertBefore(mn.instructions.getFirst(), hook);
            }
        }
    }
}
