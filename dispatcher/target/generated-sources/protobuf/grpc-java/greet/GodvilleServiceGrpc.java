package greet;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.33.1)",
    comments = "Source: godville.proto")
public final class GodvilleServiceGrpc {

  private GodvilleServiceGrpc() {}

  public static final String SERVICE_NAME = "greet.GodvilleService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<greet.LoginData,
      greet.UserLoginOuput> getLoginMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Login",
      requestType = greet.LoginData.class,
      responseType = greet.UserLoginOuput.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<greet.LoginData,
      greet.UserLoginOuput> getLoginMethod() {
    io.grpc.MethodDescriptor<greet.LoginData, greet.UserLoginOuput> getLoginMethod;
    if ((getLoginMethod = GodvilleServiceGrpc.getLoginMethod) == null) {
      synchronized (GodvilleServiceGrpc.class) {
        if ((getLoginMethod = GodvilleServiceGrpc.getLoginMethod) == null) {
          GodvilleServiceGrpc.getLoginMethod = getLoginMethod =
              io.grpc.MethodDescriptor.<greet.LoginData, greet.UserLoginOuput>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Login"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  greet.LoginData.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  greet.UserLoginOuput.getDefaultInstance()))
              .setSchemaDescriptor(new GodvilleServiceMethodDescriptorSupplier("Login"))
              .build();
        }
      }
    }
    return getLoginMethod;
  }

  private static volatile io.grpc.MethodDescriptor<greet.RegisterData,
      greet.UserRegOutput> getRegisterMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Register",
      requestType = greet.RegisterData.class,
      responseType = greet.UserRegOutput.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<greet.RegisterData,
      greet.UserRegOutput> getRegisterMethod() {
    io.grpc.MethodDescriptor<greet.RegisterData, greet.UserRegOutput> getRegisterMethod;
    if ((getRegisterMethod = GodvilleServiceGrpc.getRegisterMethod) == null) {
      synchronized (GodvilleServiceGrpc.class) {
        if ((getRegisterMethod = GodvilleServiceGrpc.getRegisterMethod) == null) {
          GodvilleServiceGrpc.getRegisterMethod = getRegisterMethod =
              io.grpc.MethodDescriptor.<greet.RegisterData, greet.UserRegOutput>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Register"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  greet.RegisterData.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  greet.UserRegOutput.getDefaultInstance()))
              .setSchemaDescriptor(new GodvilleServiceMethodDescriptorSupplier("Register"))
              .build();
        }
      }
    }
    return getRegisterMethod;
  }

  private static volatile io.grpc.MethodDescriptor<greet.ClientId,
      greet.Empty> getLogoutMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Logout",
      requestType = greet.ClientId.class,
      responseType = greet.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<greet.ClientId,
      greet.Empty> getLogoutMethod() {
    io.grpc.MethodDescriptor<greet.ClientId, greet.Empty> getLogoutMethod;
    if ((getLogoutMethod = GodvilleServiceGrpc.getLogoutMethod) == null) {
      synchronized (GodvilleServiceGrpc.class) {
        if ((getLogoutMethod = GodvilleServiceGrpc.getLogoutMethod) == null) {
          GodvilleServiceGrpc.getLogoutMethod = getLogoutMethod =
              io.grpc.MethodDescriptor.<greet.ClientId, greet.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Logout"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  greet.ClientId.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  greet.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new GodvilleServiceMethodDescriptorSupplier("Logout"))
              .build();
        }
      }
    }
    return getLogoutMethod;
  }

  private static volatile io.grpc.MethodDescriptor<greet.ClientId,
      greet.ServerIp> getStartDuelMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "StartDuel",
      requestType = greet.ClientId.class,
      responseType = greet.ServerIp.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<greet.ClientId,
      greet.ServerIp> getStartDuelMethod() {
    io.grpc.MethodDescriptor<greet.ClientId, greet.ServerIp> getStartDuelMethod;
    if ((getStartDuelMethod = GodvilleServiceGrpc.getStartDuelMethod) == null) {
      synchronized (GodvilleServiceGrpc.class) {
        if ((getStartDuelMethod = GodvilleServiceGrpc.getStartDuelMethod) == null) {
          GodvilleServiceGrpc.getStartDuelMethod = getStartDuelMethod =
              io.grpc.MethodDescriptor.<greet.ClientId, greet.ServerIp>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "StartDuel"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  greet.ClientId.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  greet.ServerIp.getDefaultInstance()))
              .setSchemaDescriptor(new GodvilleServiceMethodDescriptorSupplier("StartDuel"))
              .build();
        }
      }
    }
    return getStartDuelMethod;
  }

  private static volatile io.grpc.MethodDescriptor<greet.ClientId,
      greet.Statistic> getGetStatisticMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetStatistic",
      requestType = greet.ClientId.class,
      responseType = greet.Statistic.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<greet.ClientId,
      greet.Statistic> getGetStatisticMethod() {
    io.grpc.MethodDescriptor<greet.ClientId, greet.Statistic> getGetStatisticMethod;
    if ((getGetStatisticMethod = GodvilleServiceGrpc.getGetStatisticMethod) == null) {
      synchronized (GodvilleServiceGrpc.class) {
        if ((getGetStatisticMethod = GodvilleServiceGrpc.getGetStatisticMethod) == null) {
          GodvilleServiceGrpc.getGetStatisticMethod = getGetStatisticMethod =
              io.grpc.MethodDescriptor.<greet.ClientId, greet.Statistic>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetStatistic"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  greet.ClientId.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  greet.Statistic.getDefaultInstance()))
              .setSchemaDescriptor(new GodvilleServiceMethodDescriptorSupplier("GetStatistic"))
              .build();
        }
      }
    }
    return getGetStatisticMethod;
  }

  private static volatile io.grpc.MethodDescriptor<greet.Empty,
      greet.Empty> getCheckMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Check",
      requestType = greet.Empty.class,
      responseType = greet.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<greet.Empty,
      greet.Empty> getCheckMethod() {
    io.grpc.MethodDescriptor<greet.Empty, greet.Empty> getCheckMethod;
    if ((getCheckMethod = GodvilleServiceGrpc.getCheckMethod) == null) {
      synchronized (GodvilleServiceGrpc.class) {
        if ((getCheckMethod = GodvilleServiceGrpc.getCheckMethod) == null) {
          GodvilleServiceGrpc.getCheckMethod = getCheckMethod =
              io.grpc.MethodDescriptor.<greet.Empty, greet.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Check"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  greet.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  greet.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new GodvilleServiceMethodDescriptorSupplier("Check"))
              .build();
        }
      }
    }
    return getCheckMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GodvilleServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GodvilleServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GodvilleServiceStub>() {
        @java.lang.Override
        public GodvilleServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GodvilleServiceStub(channel, callOptions);
        }
      };
    return GodvilleServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GodvilleServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GodvilleServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GodvilleServiceBlockingStub>() {
        @java.lang.Override
        public GodvilleServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GodvilleServiceBlockingStub(channel, callOptions);
        }
      };
    return GodvilleServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GodvilleServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GodvilleServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GodvilleServiceFutureStub>() {
        @java.lang.Override
        public GodvilleServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GodvilleServiceFutureStub(channel, callOptions);
        }
      };
    return GodvilleServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class GodvilleServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void login(greet.LoginData request,
        io.grpc.stub.StreamObserver<greet.UserLoginOuput> responseObserver) {
      asyncUnimplementedUnaryCall(getLoginMethod(), responseObserver);
    }

    /**
     */
    public void register(greet.RegisterData request,
        io.grpc.stub.StreamObserver<greet.UserRegOutput> responseObserver) {
      asyncUnimplementedUnaryCall(getRegisterMethod(), responseObserver);
    }

    /**
     */
    public void logout(greet.ClientId request,
        io.grpc.stub.StreamObserver<greet.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getLogoutMethod(), responseObserver);
    }

    /**
     */
    public void startDuel(greet.ClientId request,
        io.grpc.stub.StreamObserver<greet.ServerIp> responseObserver) {
      asyncUnimplementedUnaryCall(getStartDuelMethod(), responseObserver);
    }

    /**
     */
    public void getStatistic(greet.ClientId request,
        io.grpc.stub.StreamObserver<greet.Statistic> responseObserver) {
      asyncUnimplementedUnaryCall(getGetStatisticMethod(), responseObserver);
    }

    /**
     */
    public void check(greet.Empty request,
        io.grpc.stub.StreamObserver<greet.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getCheckMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getLoginMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                greet.LoginData,
                greet.UserLoginOuput>(
                  this, METHODID_LOGIN)))
          .addMethod(
            getRegisterMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                greet.RegisterData,
                greet.UserRegOutput>(
                  this, METHODID_REGISTER)))
          .addMethod(
            getLogoutMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                greet.ClientId,
                greet.Empty>(
                  this, METHODID_LOGOUT)))
          .addMethod(
            getStartDuelMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                greet.ClientId,
                greet.ServerIp>(
                  this, METHODID_START_DUEL)))
          .addMethod(
            getGetStatisticMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                greet.ClientId,
                greet.Statistic>(
                  this, METHODID_GET_STATISTIC)))
          .addMethod(
            getCheckMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                greet.Empty,
                greet.Empty>(
                  this, METHODID_CHECK)))
          .build();
    }
  }

  /**
   */
  public static final class GodvilleServiceStub extends io.grpc.stub.AbstractAsyncStub<GodvilleServiceStub> {
    private GodvilleServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GodvilleServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GodvilleServiceStub(channel, callOptions);
    }

    /**
     */
    public void login(greet.LoginData request,
        io.grpc.stub.StreamObserver<greet.UserLoginOuput> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getLoginMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void register(greet.RegisterData request,
        io.grpc.stub.StreamObserver<greet.UserRegOutput> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRegisterMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void logout(greet.ClientId request,
        io.grpc.stub.StreamObserver<greet.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getLogoutMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void startDuel(greet.ClientId request,
        io.grpc.stub.StreamObserver<greet.ServerIp> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getStartDuelMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getStatistic(greet.ClientId request,
        io.grpc.stub.StreamObserver<greet.Statistic> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetStatisticMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void check(greet.Empty request,
        io.grpc.stub.StreamObserver<greet.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCheckMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class GodvilleServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<GodvilleServiceBlockingStub> {
    private GodvilleServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GodvilleServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GodvilleServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public greet.UserLoginOuput login(greet.LoginData request) {
      return blockingUnaryCall(
          getChannel(), getLoginMethod(), getCallOptions(), request);
    }

    /**
     */
    public greet.UserRegOutput register(greet.RegisterData request) {
      return blockingUnaryCall(
          getChannel(), getRegisterMethod(), getCallOptions(), request);
    }

    /**
     */
    public greet.Empty logout(greet.ClientId request) {
      return blockingUnaryCall(
          getChannel(), getLogoutMethod(), getCallOptions(), request);
    }

    /**
     */
    public greet.ServerIp startDuel(greet.ClientId request) {
      return blockingUnaryCall(
          getChannel(), getStartDuelMethod(), getCallOptions(), request);
    }

    /**
     */
    public greet.Statistic getStatistic(greet.ClientId request) {
      return blockingUnaryCall(
          getChannel(), getGetStatisticMethod(), getCallOptions(), request);
    }

    /**
     */
    public greet.Empty check(greet.Empty request) {
      return blockingUnaryCall(
          getChannel(), getCheckMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class GodvilleServiceFutureStub extends io.grpc.stub.AbstractFutureStub<GodvilleServiceFutureStub> {
    private GodvilleServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GodvilleServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GodvilleServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<greet.UserLoginOuput> login(
        greet.LoginData request) {
      return futureUnaryCall(
          getChannel().newCall(getLoginMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<greet.UserRegOutput> register(
        greet.RegisterData request) {
      return futureUnaryCall(
          getChannel().newCall(getRegisterMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<greet.Empty> logout(
        greet.ClientId request) {
      return futureUnaryCall(
          getChannel().newCall(getLogoutMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<greet.ServerIp> startDuel(
        greet.ClientId request) {
      return futureUnaryCall(
          getChannel().newCall(getStartDuelMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<greet.Statistic> getStatistic(
        greet.ClientId request) {
      return futureUnaryCall(
          getChannel().newCall(getGetStatisticMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<greet.Empty> check(
        greet.Empty request) {
      return futureUnaryCall(
          getChannel().newCall(getCheckMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_LOGIN = 0;
  private static final int METHODID_REGISTER = 1;
  private static final int METHODID_LOGOUT = 2;
  private static final int METHODID_START_DUEL = 3;
  private static final int METHODID_GET_STATISTIC = 4;
  private static final int METHODID_CHECK = 5;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final GodvilleServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(GodvilleServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_LOGIN:
          serviceImpl.login((greet.LoginData) request,
              (io.grpc.stub.StreamObserver<greet.UserLoginOuput>) responseObserver);
          break;
        case METHODID_REGISTER:
          serviceImpl.register((greet.RegisterData) request,
              (io.grpc.stub.StreamObserver<greet.UserRegOutput>) responseObserver);
          break;
        case METHODID_LOGOUT:
          serviceImpl.logout((greet.ClientId) request,
              (io.grpc.stub.StreamObserver<greet.Empty>) responseObserver);
          break;
        case METHODID_START_DUEL:
          serviceImpl.startDuel((greet.ClientId) request,
              (io.grpc.stub.StreamObserver<greet.ServerIp>) responseObserver);
          break;
        case METHODID_GET_STATISTIC:
          serviceImpl.getStatistic((greet.ClientId) request,
              (io.grpc.stub.StreamObserver<greet.Statistic>) responseObserver);
          break;
        case METHODID_CHECK:
          serviceImpl.check((greet.Empty) request,
              (io.grpc.stub.StreamObserver<greet.Empty>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class GodvilleServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    GodvilleServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return greet.Godville.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("GodvilleService");
    }
  }

  private static final class GodvilleServiceFileDescriptorSupplier
      extends GodvilleServiceBaseDescriptorSupplier {
    GodvilleServiceFileDescriptorSupplier() {}
  }

  private static final class GodvilleServiceMethodDescriptorSupplier
      extends GodvilleServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    GodvilleServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (GodvilleServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GodvilleServiceFileDescriptorSupplier())
              .addMethod(getLoginMethod())
              .addMethod(getRegisterMethod())
              .addMethod(getLogoutMethod())
              .addMethod(getStartDuelMethod())
              .addMethod(getGetStatisticMethod())
              .addMethod(getCheckMethod())
              .build();
        }
      }
    }
    return result;
  }
}
