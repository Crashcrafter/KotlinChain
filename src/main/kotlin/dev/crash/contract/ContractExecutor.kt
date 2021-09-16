package dev.crash.contract

object ContractExecutor {

    class DynamicClassLoader : ClassLoader() {
        fun define(bytecode: ByteArray): Class<*> {
            return super.defineClass(null, bytecode, 0, bytecode.size)
        }
    }

    fun execute(bytecode: ByteArray, methodName: String, vararg params: Any): Any {
        val loader = DynamicClassLoader()
        val c = loader.define(bytecode)
        val instance = c.getDeclaredConstructor().newInstance()
        val types = mutableListOf<Class<*>>()
        params.forEach {
            types.add(it::class.java)
        }
        val method = instance.javaClass.getMethod(methodName, *types.toTypedArray())
        return method.invoke(null, params)
    }
}