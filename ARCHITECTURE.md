# DualVerse Architecture Documentation

## Table of Contents
1. [System Overview](#system-overview)
2. [Virtualization Engine](#virtualization-engine)
3. [Security Architecture](#security-architecture)
4. [Data Management](#data-management)
5. [Network Architecture](#network-architecture)
6. [Performance Optimization](#performance-optimization)

---

## System Overview

DualVerse implements a **container-based virtualization approach** rather than full hardware emulation. This allows for near-native performance while maintaining complete isolation between the host Android system and the virtualized environment.

### Design Philosophy

The architecture follows these core principles:

1. **Minimal Overhead**: Avoid full system emulation
2. **Security First**: Complete isolation between instances
3. **User Friendly**: Abstract complexity from end users
4. **Modular Design**: Easy to extend and maintain
5. **Privacy Focused**: No external data transmission

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           DUALVERSE SYSTEM                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                        APPLICATION LAYER                          │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │  │
│  │  │  UI Layer   │  │  Account    │  │  Settings   │              │  │
│  │  │ (Compose)   │  │  Manager    │  │  Manager    │              │  │
│  │  └─────────────┘  └─────────────┘  └─────────────┘              │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                   │                                      │
│                                   ▼                                      │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                         CORE LAYER                                │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │  │
│  │  │Virtualizat. │  │  Security   │  │  Storage    │              │  │
│  │  │   Engine    │  │   Layer     │  │  Manager    │              │  │
│  │  └─────────────┘  └─────────────┘  └─────────────┘              │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                   │                                      │
│                                   ▼                                      │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                        PLATFORM LAYER                             │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │  │
│  │  │   Linux     │  │   Android   │  │   Hardware  │              │  │
│  │  │  Kernel     │  │  Framework  │  │ Abstraction │              │  │
│  │  └─────────────┘  └─────────────┘  └─────────────┘              │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Virtualization Engine

### Container Technology

DualVerse uses **Linux Containers (LXC-style)** virtualization rather than hardware emulation. This approach:

- Shares the host kernel (minimal overhead)
- Provides isolated process namespaces
- Maintains separate filesystem views
- Enables independent network stacks

### Virtual Machine Manager

```kotlin
class VirtualMachineManager(
    private val context: Context,
    private val config: VirtualMachineConfig
) {
    // VM States
    sealed class State {
        object Idle : State()
        object Starting : State()
        object Running : State()
        object Paused : State()
        object Stopped : State()
        data class Error(val message: String) : State()
    }
    
    // Core Components
    private val containerService: ContainerService
    private val memoryBridge: MemoryBridge
    private val storageIsolator: StorageIsolator
    private val networkNamespace: NetworkNamespace
    
    // Lifecycle Management
    suspend fun startVirtualMachine(): Result<VirtualMachine>
    suspend fun stopVirtualMachine(): Result<Unit>
    suspend fun pauseVirtualMachine(): Result<Unit>
    suspend fun resumeVirtualMachine(): Result<Unit>
    
    // Resource Management
    fun allocateResources(config: ResourceConfig): AllocationResult
    fun releaseResources(): Unit
    
    // Instance Management
    fun createInstance(appPackage: String): VirtualInstance
    fun destroyInstance(instanceId: String): Result<Unit>
}
```

### Container Service

The Container Service handles the creation and management of isolated containers:

```kotlin
class ContainerService(private val context: Context) {
    
    data class ContainerConfig(
        val containerId: String,
        val rootfs: File,           // Path to ROM filesystem
        val memoryLimit: Long,      // RAM allocation in MB
        val cpuShares: Int,         // CPU time allocation
        val networkMode: NetworkMode,
        val deviceMappings: List<DeviceMapping>
    )
    
    // Container Lifecycle
    fun createContainer(config: ContainerConfig): Container
    fun startContainer(containerId: String): Result<Unit>
    fun stopContainer(containerId: String): Result<Unit>
    fun destroyContainer(containerId: String): Result<Unit>
    
    // Namespace Isolation
    private fun createPidNamespace(): Namespace
    private fun createMountNamespace(): Namespace
    private fun createNetworkNamespace(): Namespace
    private fun createUserNamespace(): Namespace
    
    // Resource Isolation
    private fun setupCgroups(config: ContainerConfig): Cgroup
    private fun mountProcfs(container: Container): Unit
    private fun mountSysfs(container: Container): Unit
}
```

### Memory Bridge

Enables efficient memory sharing between host and virtual environment:

```kotlin
class MemoryBridge {
    
    // Shared Memory Regions
    private val sharedRegions = ConcurrentHashMap<String, SharedMemoryRegion>()
    
    // GPU Buffer Sharing
    private fun createGpuBuffer(size: Int): GpuBuffer
    private fun mapToVirtualSpace(buffer: GpuBuffer): Long
    
    // IPC Mechanisms
    fun createIpcChannel(): IpcChannel
    fun sendMessage(channel: IpcChannel, message: IpcMessage): Result<Unit>
    fun receiveMessage(channel: IpcChannel): Flow<IpcMessage>
    
    // Memory Optimization
    fun enableMemoryCompression(): Unit
    fun setMemoryQuota(containerId: String, quotaMB: Long): Unit
}
```

### Storage Isolator

Manages filesystem separation and virtualized storage:

```kotlin
class StorageIsolator(private val context: Context) {
    
    // Storage Paths
    private val baseDataPath: File     // /data/data/com.dualverse/
    private val containerPath: File    // /data/data/com.dualverse/containers/
    private val romCachePath: File     // /data/data/com.dualverse/rom/
    
    // Container Storage
    fun createContainerStorage(containerId: String): ContainerStorage
    fun mountOverlay(containerId: String, layers: List<File>): Unit
    fun unmountContainer(containerId: String): Unit
    
    // Data Management
    fun copyAppData(sourcePackage: String, targetContainer: String): Result<Unit>
    fun syncData(containerId: String): Result<Unit>
    fun clearContainerData(containerId: String): Result<Unit>
    
    // Encryption
    fun encryptContainerStorage(containerId: String, key: SecretKey): Unit
    fun decryptContainerStorage(containerId: String, key: SecretKey): Unit
}
```

---

## Security Architecture

### Device Identity Spoofing

Each virtual instance receives a unique, realistic device identity:

```kotlin
class DeviceSpoofer {
    
    data class VirtualDeviceIdentity(
        val imei: String,              // Random valid IMEI
        val androidId: String,         // 16-char hex string
        val serialNumber: String,      // Device serial
        val macAddress: String,        // Randomized MAC
        val ssid: String,              // Simulated SSID
        val buildFingerprint: String,  // Build identity
        val hardwareInfo: HardwareInfo // CPU, GPU, sensors
    )
    
    // Identity Generation
    fun generateIdentity(): VirtualDeviceIdentity
    fun validateIdentity(identity: VirtualDeviceIdentity): Boolean
    
    // Persistence
    fun saveIdentity(containerId: String, identity: VirtualDeviceIdentity)
    fun loadIdentity(containerId: String): VirtualDeviceIdentity?
    
    // Runtime Spoofing
    fun injectIdentity(identity: VirtualDeviceIdentity, container: Container): Unit
    
    // Hook Framework
    private fun hookSystemProperties(): Unit
    private fun hookTelephonyManager(): Unit
    private fun hookBuildClass(): Unit
}
```

### Anti-Detection System

Comprehensive bypass for common detection methods:

```kotlin
class AntiDetection {
    
    enum class DetectionMethod {
        ROOT_CHECK,           // su binary, root apps
        EMULATOR_CHECK,       // QEMU, Genymotion signatures
        DEBUGGER_CHECK,       // ptrace, JDWP
        HOOK_CHECK,           // Xposed, Frida detection
        FILE_INTEGRITY,       // APK modification
        SIGNATURE_CHECK,      // App signature verification
        BEHAVIORAL_ANALYSIS   // Timing, sensor patterns
    }
    
    // Bypass Methods
    fun bypassRootDetection(): Unit
    fun bypassEmulatorDetection(): Unit
    fun bypassDebuggerDetection(): Unit
    fun bypassHookDetection(): Unit
    
    // Behavioral Simulation
    fun simulateRealDeviceSensors(): Flow<SensorEvent>
    fun simulateRealDeviceTiming(): Unit
    
    // Configuration
    fun configureBypass(methods: Set<DetectionMethod>): Unit
}
```

### KeyStore Bridge

Secure credential storage across instances:

```kotlin
class KeyStoreBridge(private val context: Context) {
    
    // Key Management
    fun generateMasterKey(): SecretKey
    fun deriveContainerKey(containerId: String, masterKey: SecretKey): SecretKey
    
    // Secure Storage
    fun storeCredential(containerId: String, key: String, value: ByteArray): Unit
    fun retrieveCredential(containerId: String, key: String): ByteArray?
    
    // Android Keystore Integration
    private fun initializeKeystore(): Unit
    private fun createKeyEntry(alias: String): KeyStore.Entry
}
```

---

## Data Management

### Account Manager

```kotlin
class AccountManager(private val context: Context) {
    
    data class GameAccount(
        val id: String,
        val containerId: String,
        val appPackage: String,
        val displayName: String,
        val lastActive: Long,
        val autoLogin: Boolean
    )
    
    // Account Operations
    fun createAccount(appPackage: String): Result<GameAccount>
    fun deleteAccount(accountId: String): Result<Unit>
    fun listAccounts(): List<GameAccount>
    fun getAccount(accountId: String): GameAccount?
    
    // Session Management
    fun login(accountId: String): Result<Session>
    fun logout(accountId: String): Result<Unit>
    fun getActiveSession(accountId: String): Session?
}
```

### App Cloner

```kotlin
class AppCloner(private val context: Context) {
    
    data class CloneConfig(
        val sourcePackage: String,
        val targetContainer: String,
        val cloneData: Boolean,
        val cloneCache: Boolean,
        val customName: String?
    )
    
    // Cloning Operations
    fun cloneApp(config: CloneConfig): Result<ClonedApp>
    fun updateClone(cloneId: String): Result<Unit>
    fun removeClone(cloneId: String): Result<Unit>
    
    // APK Manipulation
    private fun extractApk(packageName: String): File
    private fun repackageApk(apk: File, newPackage: String): File
    private fun signApk(apk: File): File
    
    // Data Cloning
    private fun cloneAppData(source: String, target: String): Unit
    private fun cloneSharedPrefs(source: String, target: String): Unit
    private fun cloneDatabase(source: String, target: String): Unit
}
```

---

## Network Architecture

### Network Namespace

```kotlin
class NetworkNamespace {
    
    data class NetworkConfig(
        val namespaceId: String,
        val ipAddress: String,
        val gateway: String,
        val dnsServers: List<String>,
        val macAddress: String
    )
    
    // Network Isolation
    fun createNamespace(namespaceId: String): Result<NetworkNamespace>
    fun destroyNamespace(namespaceId: String): Result<Unit>
    
    // Virtual Networking
    fun createVirtualInterface(namespace: NetworkNamespace): VirtualInterface
    fun connectToHost(namespace: NetworkNamespace): Unit
    
    // Traffic Management
    fun routeTraffic(namespace: NetworkNamespace, target: String): Unit
    fun setBandwidthLimit(namespace: NetworkNamespace, kbps: Int): Unit
}
```

### MAC Randomization

```kotlin
class MacRandomizer {
    
    // MAC Generation
    fun generateRandomMac(): String
    fun generateOuiMac(oui: String): String  // Vendor-specific MAC
    
    // Management
    fun assignMacToInterface(interface: VirtualInterface): Unit
    fun rotateMac(containerId: String): Unit
    
    // Persistence
    fun saveMacMapping(containerId: String, mac: String): Unit
    fun loadMacMapping(containerId: String): String?
}
```

---

## Performance Optimization

### Resource Allocation Strategy

```kotlin
class ResourceManager {
    
    data class ResourceQuota(
        val memoryMB: Long,        // RAM allocation
        val cpuShares: Int,        // CPU time percentage
        val storageMB: Long,       // Storage limit
        val networkMbps: Int       // Network bandwidth
    )
    
    // Dynamic Allocation
    fun calculateOptimalQuota(available: Resources): ResourceQuota
    fun adjustQuota(containerId: String, newQuota: ResourceQuota): Unit
    
    // Monitoring
    fun monitorResourceUsage(containerId: String): Flow<ResourceUsage>
    fun detectResourcePressure(): PressureLevel
    
    // Optimization
    fun optimizeForGame(containerId: String, gamePackage: String): Unit
    fun enableGpuAcceleration(containerId: String): Unit
}
```

### Memory Optimization Techniques

1. **Memory Deduplication**: Share identical memory pages across instances
2. **Lazy Loading**: Load ROM components on demand
3. **Memory Compression**: Compress inactive memory regions
4. **Garbage Collection Tuning**: Optimized GC for containerized apps

### GPU Acceleration

```kotlin
class GpuAccelerator {
    
    // GPU Passthrough
    fun enablePassthrough(containerId: String): Result<Unit>
    fun createVirtualGpu(containerId: String): VirtualGpu
    
    // Rendering
    fun setupRenderServer(): RenderServer
    fun createRenderNode(): RenderNode
    
    // Performance
    fun setRenderQuality(containerId: String, quality: Quality): Unit
    fun enableVsync(containerId: String, enabled: Boolean): Unit
}
```

---

## ROM Structure

### Minimal Android 11 ROM

The custom ROM contains only essential components:

```
rom/
├── system/
│   ├── app/                    # Essential system apps only
│   │   ├── Settings/
│   │   └── PackageInstaller/
│   ├── priv-app/               # Privileged apps
│   │   └── DualVerseService/
│   ├── framework/              # Android framework JARs
│   │   ├── framework.jar
│   │   ├── services.jar
│   │   └── core-oj.jar
│   ├── lib64/                  # Native libraries
│   │   ├── libandroid_runtime.so
│   │   ├── libbinder.so
│   │   └── libgui.so
│   ├── bin/                    # Native executables
│   │   ├── app_process64
│   │   └── linker64
│   ├── etc/
│   │   ├── permissions/        # Permission definitions
│   │   └── default.prop        # System properties
│   └── build.prop              # Build configuration
├── data/
│   └── system/                 # System data
└── vendor/
    └── etc/                    # Vendor configurations
```

### Size Optimization

| Component | Original Size | Optimized Size |
|-----------|--------------|----------------|
| System Framework | ~800MB | ~120MB |
| System Apps | ~400MB | ~30MB |
| Native Libraries | ~200MB | ~40MB |
| Resources | ~300MB | ~10MB |
| **Total** | **~1.7GB** | **~200MB** |

---

## Security Considerations

### Threat Model

1. **Game Anti-Cheat Systems**: Detecting virtualization
2. **Root Detection**: Checking for elevated privileges
3. **Emulator Detection**: Identifying emulated environments
4. **Behavioral Analysis**: Pattern-based detection

### Mitigations

| Threat | Mitigation |
|--------|------------|
| Root Detection | No root required; virtualized environment |
| Emulator Detection | Realistic device fingerprints |
| Behavioral Analysis | Sensor and timing simulation |
| Signature Verification | Transparent APK re-signing |
| Memory Analysis | Encrypted memory regions |

---

## Future Enhancements

1. **Multi-Instance Support**: Run 3+ instances simultaneously
2. **Cloud ROM Updates**: Seamless ROM version updates
3. **Plugin System**: Extend functionality with plugins
4. **Remote Management**: Control instances remotely
5. **Backup/Sync**: Cloud backup for account data
