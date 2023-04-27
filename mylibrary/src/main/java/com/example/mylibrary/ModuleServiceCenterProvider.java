package com.example.mylibrary;

public class ModuleServiceCenterProvider {
    private ModuleServiceCenter moduleServiceCenter;

    private ModuleServiceCenterProvider() {

    }

    public static ModuleServiceCenterProvider getInstance() {
        return ModuleServiceCenterProviderHolder.provider;
    }

    private static class ModuleServiceCenterProviderHolder {
        private static final ModuleServiceCenterProvider provider = new ModuleServiceCenterProvider();
    }

    public void setModuleServiceCenter(ModuleServiceCenter moduleServiceCenter) {
        this.moduleServiceCenter = moduleServiceCenter;
    }

    public ModuleServiceCenter getModuleServiceCenter() {
        return moduleServiceCenter;
    }
}
