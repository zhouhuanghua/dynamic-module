package cn.zhh.dynamic_module;

class ModuleRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ModuleRuntimeException(String message) {
        super(message);
    }

    public ModuleRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

}