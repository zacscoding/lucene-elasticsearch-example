package org.esdemo.web;

import java.io.File;
import org.esdemo.repository.MappingTestRepository;
import org.esdemo.util.ClassFileDumpUtil;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zacconding
 * @Date 2018-04-06
 * @GitHub : https://github.com/zacscoding
 */
@RestController
public class FrontController {
    @Autowired
    MappingTestRepository repository;

    @GetMapping("/")
    public String test() throws Exception {
        String className = repository.getClass().getName();
        System.out.println("## check " + className);
        ClassLoader loader = MappingTestRepository.class.getClassLoader();
        ClassReader cr = new ClassReader(loader.getResourceAsStream(className.replace('.', '/') + ".class"));
        ClassWriter cw = new ClassWriter(cr, 0);
        cr.accept(new ClassVisitor(Opcodes.ASM5) {
            @Override
            public void visitSource(String s, String s1) {
                super.visitSource(s, s1);
            }
        },0);

        byte[] bytes = cw.toByteArray();
        ClassFileDumpUtil.writeByteCode(bytes, new File("D:\test", className + ".class"));
        return "test";
    }


}
