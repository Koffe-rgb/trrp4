// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: godville.proto

package greet;

public interface ClientDataOrBuilder extends
    // @@protoc_insertion_point(interface_extends:greet.ClientData)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int64 id = 1;</code>
   * @return The id.
   */
  long getId();

  /**
   * <code>string nickname = 2;</code>
   * @return The nickname.
   */
  java.lang.String getNickname();
  /**
   * <code>string nickname = 2;</code>
   * @return The bytes for nickname.
   */
  com.google.protobuf.ByteString
      getNicknameBytes();

  /**
   * <code>int64 healthCount = 3;</code>
   * @return The healthCount.
   */
  long getHealthCount();

  /**
   * <code>string heroName = 4;</code>
   * @return The heroName.
   */
  java.lang.String getHeroName();
  /**
   * <code>string heroName = 4;</code>
   * @return The bytes for heroName.
   */
  com.google.protobuf.ByteString
      getHeroNameBytes();
}