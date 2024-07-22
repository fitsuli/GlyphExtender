package com.nothing.thirdparty

import android.os.Binder
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import android.os.RemoteException

/* loaded from: KetchumSDK_Community_20240307.jar:com/nothing/thirdparty/IGlyphService.class */
interface IGlyphService : IInterface {
    @Throws(RemoteException::class)
    fun setFrameColors(iArr: IntArray?)

    @Throws(RemoteException::class)
    fun openSession()

    @Throws(RemoteException::class)
    fun closeSession()

    @Throws(RemoteException::class)
    fun register(str: String?): Boolean

    @Throws(RemoteException::class)
    fun registerSDK(str: String?, str2: String?): Boolean

    /* loaded from: KetchumSDK_Community_20240307.jar:com/nothing/thirdparty/IGlyphService$Default.class */
    class Default : IGlyphService {
        @Throws(RemoteException::class)  // com.nothing.thirdparty.IGlyphService
        override fun setFrameColors(colors: IntArray?) {
        }

        @Throws(RemoteException::class)  // com.nothing.thirdparty.IGlyphService
        override fun openSession() {
        }

        @Throws(RemoteException::class)  // com.nothing.thirdparty.IGlyphService
        override fun closeSession() {
        }

        @Throws(RemoteException::class)  // com.nothing.thirdparty.IGlyphService
        override fun register(key: String?): Boolean {
            return false
        }

        @Throws(RemoteException::class)  // com.nothing.thirdparty.IGlyphService
        override fun registerSDK(key: String?, device: String?): Boolean {
            return false
        }

        // android.os.IInterface
        override fun asBinder(): IBinder? {
            return null
        }
    }

    /* loaded from: KetchumSDK_Community_20240307.jar:com/nothing/thirdparty/IGlyphService$Stub.class */
    abstract class Stub : Binder(), IGlyphService {
        init {
            attachInterface(this, DESCRIPTOR)
        }

        // android.os.IInterface
        override fun asBinder(): IBinder {
            return this
        }

        @Throws(RemoteException::class)  // android.os.Binder
        public override fun onTransact(
            code: Int,
            data: Parcel,
            reply: Parcel?,
            flags: Int,
        ): Boolean {
            if (code >= TRANSACTION_setFrameColors && code <= 16777215) {
                data.enforceInterface(DESCRIPTOR)
            }
            when (code) {
                1598968902 -> {
                    reply!!.writeString(DESCRIPTOR)
                    return true
                }

                else -> when (code) {
                    TRANSACTION_setFrameColors -> {
                        val _arg0 = data.createIntArray()
                        data.enforceNoDataAvail()
                        setFrameColors(_arg0)
                        reply!!.writeNoException()
                        return true
                    }

                    TRANSACTION_openSession -> {
                        openSession()
                        reply!!.writeNoException()
                        return true
                    }

                    TRANSACTION_closeSession -> {
                        closeSession()
                        reply!!.writeNoException()
                        return true
                    }

                    TRANSACTION_register -> {
                        val _arg02 = data.readString()
                        data.enforceNoDataAvail()
                        val _result = register(_arg02)
                        reply!!.writeNoException()
                        reply.writeBoolean(_result)
                        return true
                    }

                    TRANSACTION_registerSDK -> {
                        val _arg03 = data.readString()
                        val _arg1 = data.readString()
                        data.enforceNoDataAvail()
                        val _result2 = registerSDK(_arg03, _arg1)
                        reply!!.writeNoException()
                        reply.writeBoolean(_result2)
                        return true
                    }

                    else -> return super.onTransact(code, data, reply, flags)
                }
            }
        }

        /* loaded from: KetchumSDK_Community_20240307.jar:com/nothing/thirdparty/IGlyphService$Stub$Proxy.class */
        private class Proxy(private val mRemote: IBinder) : IGlyphService {
            // android.os.IInterface
            override fun asBinder(): IBinder {
                return this.mRemote
            }

            val interfaceDescriptor: String
                get() = DESCRIPTOR

            @Throws(RemoteException::class)  // com.nothing.thirdparty.IGlyphService
            override fun setFrameColors(colors: IntArray?) {
                val _data = Parcel.obtain()
                val _reply = Parcel.obtain()
                try {
                    _data.writeInterfaceToken(DESCRIPTOR)
                    _data.writeIntArray(colors)
                    mRemote.transact(TRANSACTION_setFrameColors, _data, _reply, 0)
                    _reply.readException()
                    _reply.recycle()
                    _data.recycle()
                } catch (th: Throwable) {
                    _reply.recycle()
                    _data.recycle()
                    throw th
                }
            }

            @Throws(RemoteException::class)  // com.nothing.thirdparty.IGlyphService
            override fun openSession() {
                val _data = Parcel.obtain()
                val _reply = Parcel.obtain()
                try {
                    _data.writeInterfaceToken(DESCRIPTOR)
                    mRemote.transact(TRANSACTION_openSession, _data, _reply, 0)
                    _reply.readException()
                    _reply.recycle()
                    _data.recycle()
                } catch (th: Throwable) {
                    _reply.recycle()
                    _data.recycle()
                    throw th
                }
            }

            @Throws(RemoteException::class)  // com.nothing.thirdparty.IGlyphService
            override fun closeSession() {
                val _data = Parcel.obtain()
                val _reply = Parcel.obtain()
                try {
                    _data.writeInterfaceToken(DESCRIPTOR)
                    mRemote.transact(TRANSACTION_closeSession, _data, _reply, 0)
                    _reply.readException()
                    _reply.recycle()
                    _data.recycle()
                } catch (th: Throwable) {
                    _reply.recycle()
                    _data.recycle()
                    throw th
                }
            }

            @Throws(RemoteException::class)  // com.nothing.thirdparty.IGlyphService
            override fun register(key: String?): Boolean {
                val _data = Parcel.obtain()
                val _reply = Parcel.obtain()
                try {
                    _data.writeInterfaceToken(DESCRIPTOR)
                    _data.writeString(key)
                    mRemote.transact(TRANSACTION_register, _data, _reply, 0)
                    _reply.readException()
                    val _result = _reply.readBoolean()
                    _reply.recycle()
                    _data.recycle()
                    return _result
                } catch (th: Throwable) {
                    _reply.recycle()
                    _data.recycle()
                    throw th
                }
            }

            @Throws(RemoteException::class)  // com.nothing.thirdparty.IGlyphService
            override fun registerSDK(key: String?, device: String?): Boolean {
                val _data = Parcel.obtain()
                val _reply = Parcel.obtain()
                try {
                    _data.writeInterfaceToken(DESCRIPTOR)
                    _data.writeString(key)
                    _data.writeString(device)
                    mRemote.transact(TRANSACTION_registerSDK, _data, _reply, 0)
                    _reply.readException()
                    val _result = _reply.readBoolean()
                    _reply.recycle()
                    _data.recycle()
                    return _result
                } catch (th: Throwable) {
                    _reply.recycle()
                    _data.recycle()
                    throw th
                }
            }
        }

        companion object {
            const val TRANSACTION_setFrameColors: Int = 1
            const val TRANSACTION_openSession: Int = 2
            const val TRANSACTION_closeSession: Int = 3
            const val TRANSACTION_register: Int = 4
            const val TRANSACTION_registerSDK: Int = 5

            fun asInterface(obj: IBinder?): IGlyphService? {
                if (obj == null) {
                    return null
                }
                val iin = obj.queryLocalInterface(DESCRIPTOR)
                if (iin != null && (iin is IGlyphService)) {
                    return iin
                }
                return Proxy(obj)
            }
        }
    }

    companion object {
        const val DESCRIPTOR: String = "com.nothing.thirdparty.IGlyphService"
    }
}
