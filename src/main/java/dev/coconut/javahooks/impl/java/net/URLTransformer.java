package dev.coconut.javahooks.impl.java.net;

import dev.coconut.javahooks.HookStatus;
import dev.coconut.javahooks.IClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.net.URL;

public class URLTransformer extends IClassTransformer {
    public URLTransformer() {
        super("java/net/URL");
    }

    public static native HookStatus nativeHook(URL url);

    @Override
    public void process(ClassNode classNode) {
        for (MethodNode mn : classNode.methods) {
            if ((mn.name.equals("openConnection") && mn.desc.equals("()Ljava/net/URLConnection;")) ||
                    (mn.name.equals("openStream") && mn.desc.equals("()Ljava/io/InputStream;"))) {
                InsnList hook = new InsnList();
                hook.add(new VarInsnNode(Opcodes.ALOAD, 0));
                hook.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "dev/coconut/javahooks/impl/java/net/URLTransformer",
                        "nativeHook",
                        "(Ljava/net/URL;)Ldev/coconut/javahooks/HookStatus;",
                        false
                ));
                hook.add(new FieldInsnNode(
                        Opcodes.GETSTATIC,
                        "dev/coconut/javahooks/HookStatus",
                        "CANCEL",
                        "Ldev/coconut/javahooks/HookStatus;"
                ));

                LabelNode passLabel = new LabelNode();
                hook.add(new JumpInsnNode(Opcodes.IF_ACMPNE, passLabel));

                hook.add(new TypeInsnNode(Opcodes.NEW, "java/lang/RuntimeException"));
                hook.add(new InsnNode(Opcodes.DUP));
                hook.add(new LdcInsnNode("coconut: prevented url"));
                hook.add(new MethodInsnNode(
                        Opcodes.INVOKESPECIAL,
                        "java/lang/RuntimeException",
                        "<init>",
                        "(Ljava/lang/String;)V",
                        false
                ));
                hook.add(new InsnNode(Opcodes.ATHROW));
                hook.add(passLabel);

                mn.instructions.insertBefore(mn.instructions.getFirst(), hook);
            }
        }
    }
}
