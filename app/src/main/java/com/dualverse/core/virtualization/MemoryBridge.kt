package com.dualverse.core.virtualization

/**
 * Memory bridge for efficient memory sharing between host and virtual environment.
 * Handles GPU buffer sharing and IPC mechanisms.
 */
class MemoryBridge {

    private val sharedRegions = mutableMapOf<String, SharedMemoryRegion>()

    data class SharedMemoryRegion(
        val id: String,
        val size: Int,
        val address: Long,
        val refCount: Int
    )

    /**
     * Creates a shared memory region.
     */
    fun createSharedRegion(id: String, size: Int): SharedMemoryRegion {
        val region = SharedMemoryRegion(
            id = id,
            size = size,
            address = nativeAllocateMemory(size),
            refCount = 1
        )
        sharedRegions[id] = region
        return region
    }

    /**
     * Maps a memory region to the virtual address space.
     */
    fun mapToVirtualSpace(regionId: String): Long {
        val region = sharedRegions[regionId] ?: return -1
        return nativeMapToVirtualSpace(region.address, region.size)
    }

    /**
     * Creates an IPC channel for communication.
     */
    fun createIpcChannel(): IpcChannel {
        return IpcChannel(nativeCreateIpcChannel())
    }

    /**
     * Cleans up all shared memory regions.
     */
    fun cleanup() {
        sharedRegions.values.forEach { region ->
            nativeFreeMemory(region.address)
        }
        sharedRegions.clear()
    }

    private external fun nativeAllocateMemory(size: Int): Long
    private external fun nativeFreeMemory(address: Long)
    private external fun nativeMapToVirtualSpace(address: Long, size: Int): Long
    private external fun nativeCreateIpcChannel(): Int

    companion object {
        init {
            System.loadLibrary("dualverse-native")
        }
    }
}

/**
 * IPC Channel for communication between host and virtual environment.
 */
class IpcChannel(private val channelId: Int) {

    /**
     * Sends a message through the channel.
     */
    fun sendMessage(message: ByteArray): Boolean {
        return nativeSendMessage(channelId, message)
    }

    /**
     * Receives a message from the channel.
     */
    fun receiveMessage(): ByteArray? {
        return nativeReceiveMessage(channelId)
    }

    /**
     * Closes the IPC channel.
     */
    fun close() {
        nativeCloseChannel(channelId)
    }

    private external fun nativeSendMessage(channelId: Int, message: ByteArray): Boolean
    private external fun nativeReceiveMessage(channelId: Int): ByteArray?
    private external fun nativeCloseChannel(channelId: Int)

    companion object {
        init {
            System.loadLibrary("dualverse-native")
        }
    }
}
