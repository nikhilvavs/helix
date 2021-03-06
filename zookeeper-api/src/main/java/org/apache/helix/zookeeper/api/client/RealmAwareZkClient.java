package org.apache.helix.zookeeper.api.client;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.helix.zookeeper.zkclient.DataUpdater;
import org.apache.helix.zookeeper.zkclient.IZkChildListener;
import org.apache.helix.zookeeper.zkclient.IZkDataListener;
import org.apache.helix.zookeeper.zkclient.IZkStateListener;
import org.apache.helix.zookeeper.zkclient.callback.ZkAsyncCallbacks;
import org.apache.helix.zookeeper.zkclient.exception.ZkTimeoutException;
import org.apache.helix.zookeeper.zkclient.serialize.BasicZkSerializer;
import org.apache.helix.zookeeper.zkclient.serialize.PathBasedZkSerializer;
import org.apache.helix.zookeeper.zkclient.serialize.SerializableSerializer;
import org.apache.helix.zookeeper.zkclient.serialize.ZkSerializer;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Op;
import org.apache.zookeeper.OpResult;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;


public interface RealmAwareZkClient {
  int DEFAULT_OPERATION_TIMEOUT = Integer.MAX_VALUE;
  int DEFAULT_CONNECTION_TIMEOUT = 60 * 1000;
  int DEFAULT_SESSION_TIMEOUT = 30 * 1000;

  // listener subscription
  List<String> subscribeChildChanges(String path, IZkChildListener listener);

  void unsubscribeChildChanges(String path, IZkChildListener listener);

  void subscribeDataChanges(String path, IZkDataListener listener);

  void unsubscribeDataChanges(String path, IZkDataListener listener);

  /*
   * This is for backwards compatibility.
   *
   * TODO: remove below default implementation when getting rid of I0Itec in the new zk client.
   */
  default void subscribeStateChanges(final IZkStateListener listener) {
    subscribeStateChanges(new HelixZkClient.I0ItecIZkStateListenerHelixImpl(listener));
  }

  /*
   * This is for backwards compatibility.
   *
   * TODO: remove below default implementation when getting rid of I0Itec in the new zk client.
   */
  default void unsubscribeStateChanges(IZkStateListener listener) {
    unsubscribeStateChanges(new HelixZkClient.I0ItecIZkStateListenerHelixImpl(listener));
  }

  /**
   * Subscribes state changes for a
   * {@link org.apache.helix.zookeeper.zkclient.deprecated.IZkStateListener} listener.
   * @deprecated
   *             This is deprecated. It is kept for backwards compatibility. Please use
   *             {@link #subscribeStateChanges(IZkStateListener)}.
   * @param listener {@link org.apache.helix.zookeeper.zkclient.deprecated.IZkStateListener}
   *          listener
   */
  @Deprecated
  void subscribeStateChanges(
      final org.apache.helix.zookeeper.zkclient.deprecated.IZkStateListener listener);

  /**
   * Unsubscribes state changes for a
   * {@link org.apache.helix.zookeeper.zkclient.deprecated.IZkStateListener} listener.
   * @deprecated
   *             This is deprecated. It is kept for backwards compatibility. Please use
   *             {@link #unsubscribeStateChanges(IZkStateListener)}.
   * @param listener {@link org.apache.helix.zookeeper.zkclient.deprecated.IZkStateListener}
   *          listener
   */
  @Deprecated
  void unsubscribeStateChanges(
      org.apache.helix.zookeeper.zkclient.deprecated.IZkStateListener listener);

  void unsubscribeAll();

  // data access
  void createPersistent(String path);

  void createPersistent(String path, boolean createParents);

  void createPersistent(String path, boolean createParents, List<ACL> acl);

  void createPersistent(String path, Object data);

  void createPersistent(String path, Object data, List<ACL> acl);

  String createPersistentSequential(String path, Object data);

  String createPersistentSequential(String path, Object data, List<ACL> acl);

  void createEphemeral(final String path);

  void createEphemeral(final String path, final String sessionId);

  void createEphemeral(final String path, final List<ACL> acl);

  void createEphemeral(final String path, final List<ACL> acl, final String sessionId);

  String create(final String path, Object data, final CreateMode mode);

  String create(final String path, Object datat, final List<ACL> acl, final CreateMode mode);

  void createEphemeral(final String path, final Object data);

  void createEphemeral(final String path, final Object data, final String sessionId);

  void createEphemeral(final String path, final Object data, final List<ACL> acl);

  void createEphemeral(final String path, final Object data, final List<ACL> acl,
      final String sessionId);

  String createEphemeralSequential(final String path, final Object data);

  String createEphemeralSequential(final String path, final Object data, final List<ACL> acl);

  String createEphemeralSequential(final String path, final Object data, final String sessionId);

  String createEphemeralSequential(final String path, final Object data, final List<ACL> acl,
      final String sessionId);

  List<String> getChildren(String path);

  int countChildren(String path);

  boolean exists(final String path);

  Stat getStat(final String path);

  boolean waitUntilExists(String path, TimeUnit timeUnit, long time);

  void deleteRecursively(String path);

  boolean delete(final String path);

  <T extends Object> T readData(String path);

  <T extends Object> T readData(String path, boolean returnNullIfPathNotExists);

  <T extends Object> T readData(String path, Stat stat);

  <T extends Object> T readData(final String path, final Stat stat, final boolean watch);

  <T extends Object> T readDataAndStat(String path, Stat stat, boolean returnNullIfPathNotExists);

  void writeData(String path, Object object);

  <T extends Object> void updateDataSerialized(String path, DataUpdater<T> updater);

  void writeData(final String path, Object datat, final int expectedVersion);

  Stat writeDataReturnStat(final String path, Object datat, final int expectedVersion);

  Stat writeDataGetStat(final String path, Object datat, final int expectedVersion);

  void asyncCreate(final String path, Object datat, final CreateMode mode,
      final ZkAsyncCallbacks.CreateCallbackHandler cb);

  void asyncSetData(final String path, Object datat, final int version,
      final ZkAsyncCallbacks.SetDataCallbackHandler cb);

  void asyncGetData(final String path, final ZkAsyncCallbacks.GetDataCallbackHandler cb);

  void asyncExists(final String path, final ZkAsyncCallbacks.ExistsCallbackHandler cb);

  void asyncDelete(final String path, final ZkAsyncCallbacks.DeleteCallbackHandler cb);

  void watchForData(final String path);

  List<String> watchForChilds(final String path);

  long getCreationTime(String path);

  List<OpResult> multi(final Iterable<Op> ops);

  // ZK state control
  boolean waitUntilConnected(long time, TimeUnit timeUnit);

  /**
   * Waits for SyncConnected state and returns a valid session ID(non-zero). The implementation of
   * this method should wait for SyncConnected state and ZK session to be established, and should
   * guarantee the established session's ID is returned before keeper state changes.
   *
   * Please note: this default implementation may have race condition issue and return an unexpected
   * session ID that is zero or another new session's ID. The default implementation is for backward
   * compatibility purpose.
   *
   * @param timeout Max waiting time for connecting to ZK server.
   * @param timeUnit Time unit for the timeout.
   * @return A valid ZK session ID which is non-zero.
   */
  default long waitForEstablishedSession(long timeout, TimeUnit timeUnit) {
    if (!waitUntilConnected(timeout, timeUnit)) {
      throw new ZkTimeoutException(
          "Failed to get established session because connecting to ZK server has timed out in "
              + timeout + " " + timeUnit);
    }
    return getSessionId();
  }

  String getServers();

  long getSessionId();

  void close();

  boolean isClosed();

  // other
  byte[] serialize(Object data, String path);

  <T extends Object> T deserialize(byte[] data, String path);

  void setZkSerializer(ZkSerializer zkSerializer);

  void setZkSerializer(PathBasedZkSerializer zkSerializer);

  PathBasedZkSerializer getZkSerializer();

  /**
   * A class that wraps a default implementation of
   * {@link IZkStateListener}, which means this listener
   * runs the methods of {@link IZkStateListener}.
   * This is for backward compatibility and to avoid breaking the original implementation of
   * {@link org.apache.helix.zookeeper.zkclient.deprecated.IZkStateListener}.
   */
  class I0ItecIZkStateListenerHelixImpl
      implements org.apache.helix.zookeeper.zkclient.deprecated.IZkStateListener {
    private IZkStateListener _listener;

    I0ItecIZkStateListenerHelixImpl(IZkStateListener listener) {
      _listener = listener;
    }

    @Override
    public void handleStateChanged(Watcher.Event.KeeperState keeperState) throws Exception {
      _listener.handleStateChanged(keeperState);
    }

    @Override
    public void handleNewSession() throws Exception {
      /*
       * org.apache.helix.manager.zk.zookeeper.IZkStateListener does not have handleNewSession(),
       * so null is passed into handleNewSession(sessionId).
       */
      _listener.handleNewSession(null);
    }

    @Override
    public void handleSessionEstablishmentError(Throwable error) throws Exception {
      _listener.handleSessionEstablishmentError(error);
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (!(obj instanceof HelixZkClient.I0ItecIZkStateListenerHelixImpl)) {
        return false;
      }
      if (_listener == null) {
        return false;
      }

      HelixZkClient.I0ItecIZkStateListenerHelixImpl
          defaultListener = (HelixZkClient.I0ItecIZkStateListenerHelixImpl) obj;

      return _listener.equals(defaultListener._listener);
    }

    @Override
    public int hashCode() {
      /*
       * The original listener's hashcode helps find the wrapped listener with the same original
       * listener. This is helpful in unsubscribeStateChanges(listener).
       */
      return _listener.hashCode();
    }
  }

  /**
   * Configuration for creating a new ZkConnection.
   */
  class ZkConnectionConfig {
    // Connection configs
    private final String _zkServers;
    private int _sessionTimeout = HelixZkClient.DEFAULT_SESSION_TIMEOUT;

    public ZkConnectionConfig(String zkServers) {
      _zkServers = zkServers;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (!(obj instanceof HelixZkClient.ZkConnectionConfig)) {
        return false;
      }
      HelixZkClient.ZkConnectionConfig configObj = (HelixZkClient.ZkConnectionConfig) obj;
      return (_zkServers == null && configObj._zkServers == null ||
          _zkServers != null && _zkServers.equals(configObj._zkServers)) &&
          _sessionTimeout == configObj._sessionTimeout;
    }

    @Override
    public int hashCode() {
      return _sessionTimeout * 31 + _zkServers.hashCode();
    }

    @Override
    public String toString() {
      return (_zkServers + "_" + _sessionTimeout).replaceAll("[\\W]", "_");
    }

    public HelixZkClient.ZkConnectionConfig setSessionTimeout(Integer sessionTimeout) {
      this._sessionTimeout = sessionTimeout;
      return this;
    }

    public String getZkServers() {
      return _zkServers;
    }

    public int getSessionTimeout() {
      return _sessionTimeout;
    }
  }

  /**
   * Configuration for creating a new RealmAwareZkClient with serializer and monitor.
   */
  class ZkClientConfig {
    // For client to init the connection
    private long _connectInitTimeout = HelixZkClient.DEFAULT_CONNECTION_TIMEOUT;

    // Data access configs
    private long _operationRetryTimeout = HelixZkClient.DEFAULT_OPERATION_TIMEOUT;

    // Others
    private PathBasedZkSerializer _zkSerializer;

    // Monitoring
    private String _monitorType;
    private String _monitorKey;
    private String _monitorInstanceName = null;
    private boolean _monitorRootPathOnly = true;

    public HelixZkClient.ZkClientConfig setZkSerializer(PathBasedZkSerializer zkSerializer) {
      this._zkSerializer = zkSerializer;
      return this;
    }

    public HelixZkClient.ZkClientConfig setZkSerializer(ZkSerializer zkSerializer) {
      this._zkSerializer = new BasicZkSerializer(zkSerializer);
      return this;
    }

    /**
     * Used as part of the MBean ObjectName. This item is required for enabling monitoring.
     *
     * @param monitorType
     */
    public HelixZkClient.ZkClientConfig setMonitorType(String monitorType) {
      this._monitorType = monitorType;
      return this;
    }

    /**
     * Used as part of the MBean ObjectName. This item is required for enabling monitoring.
     *
     * @param monitorKey
     */
    public HelixZkClient.ZkClientConfig setMonitorKey(String monitorKey) {
      this._monitorKey = monitorKey;
      return this;
    }

    /**
     * Used as part of the MBean ObjectName. This item is optional.
     *
     * @param instanceName
     */
    public HelixZkClient.ZkClientConfig setMonitorInstanceName(String instanceName) {
      this._monitorInstanceName = instanceName;
      return this;
    }

    public HelixZkClient.ZkClientConfig setMonitorRootPathOnly(Boolean monitorRootPathOnly) {
      this._monitorRootPathOnly = monitorRootPathOnly;
      return this;
    }

    public HelixZkClient.ZkClientConfig setOperationRetryTimeout(Long operationRetryTimeout) {
      this._operationRetryTimeout = operationRetryTimeout;
      return this;
    }

    public HelixZkClient.ZkClientConfig setConnectInitTimeout(long _connectInitTimeout) {
      this._connectInitTimeout = _connectInitTimeout;
      return this;
    }

    public PathBasedZkSerializer getZkSerializer() {
      if (_zkSerializer == null) {
        _zkSerializer = new BasicZkSerializer(new SerializableSerializer());
      }
      return _zkSerializer;
    }

    public long getOperationRetryTimeout() {
      return _operationRetryTimeout;
    }

    public String getMonitorType() {
      return _monitorType;
    }

    public String getMonitorKey() {
      return _monitorKey;
    }

    public String getMonitorInstanceName() {
      return _monitorInstanceName;
    }

    public boolean isMonitorRootPathOnly() {
      return _monitorRootPathOnly;
    }

    public long getConnectInitTimeout() {
      return _connectInitTimeout;
    }
  }
}
