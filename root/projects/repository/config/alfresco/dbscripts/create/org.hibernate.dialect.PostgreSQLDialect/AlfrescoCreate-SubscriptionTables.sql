--
-- Title:      Subscription tables
-- Database:   PostgreSQL
-- Since:      V4.0 Schema 5011
-- Author:     Florian Mueller
--
-- Please contact support@alfresco.com if you need assistance with the upgrade.
--

CREATE TABLE alf_subscriptions
(
  user_node_id INT8 NOT NULL,
  node_id INT8 NOT NULL,
  PRIMARY KEY (user_node_id, node_id),
  CONSTRAINT fk_alf_sub_user FOREIGN KEY (user_node_id) REFERENCES alf_node(id) ON DELETE CASCADE,
  CONSTRAINT fk_alf_sub_node FOREIGN KEY (node_id) REFERENCES alf_node(id) ON DELETE CASCADE
);
CREATE INDEX fk_alf_sub_node ON alf_subscriptions (node_id);
