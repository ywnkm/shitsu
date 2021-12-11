package net.ywnkm.shitsu.utils

import org.yaml.snakeyaml.Yaml
import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarFile
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.superclasses

public object JarUtils {

    private const val manifestLocation = "META-INF/MANIFEST.MF"

    public val manifest: Map<String, String?> by lazy {
        javaClass.getResourceAsStream(manifestLocation).use {
            if (it !== null) Yaml().load(it)
            else mapOf()
        }
    }

    @ShitsuExperimental
    public fun loadJar(
        file: File,
        classLoader: ClassLoader = URLClassLoader(arrayOf(file.toURI().toURL()))
    ): KClassList {
        val result = mutableListOf<KClass<*>>()
        val jarFile = JarFile(file)
        val classNames = mutableListOf<String>()
        for (entry in jarFile.entries()) {
            entry.name.apply {
                if (endsWith(".class")) {
                    val index = lastIndexOf(".")
                    classNames.add(replace("/",".").slice(0 until index))
                }
            }
        }
        for (className in classNames) {
            try {
                result.add(classLoader.loadClass(className).kotlin)
            } catch (e: Throwable) { e.printStackTrace() }
        }
        return KClassList(result)
    }

    @ShitsuExperimental
    public fun loadJarFromDir(dir: File): KClassList {
        val files = dir.listFiles()
            ?.filter { it.name.endsWith(".jar") }
            ?.also { if (it.isEmpty()) return KClassList.Empty }
            ?: return KClassList.Empty
        val urls = files.map { it.toURI().toURL() }
        val classLoader = URLClassLoader(urls.toTypedArray())
        val result = mutableListOf<KClass<*>>()
        for (file in files) {
            result += loadJar(file, classLoader).value
        }
        return KClassList(result)
    }

    @JvmInline
    public value class KClassList(public val value: List<KClass<*>>) {

        public inline fun <reified T : Any> onObject(block: (T) -> Unit) {
            onClass<T> {
                try {
                    it.objectInstance?.let(block)
                } catch (ignore: Throwable) { }
            }
        }

        @Suppress("Unchecked_Cast")
        public inline fun <reified T : Any> onClass(block: (KClass<T>) -> Unit) {
            for (_class in value) {
                try {
                    (_class as? KClass<T>)?.let(block)
                } catch (ignore: Throwable) { }
            }
        }

        public companion object {
            @JvmStatic
            public val Empty: KClassList = KClassList(emptyList())
        }
    }

}
