
-- ----------------------------------------------------------------------------------------------------------------
-- Create user
--
CREATE USER 'gameserver-api-user'@'localhost' IDENTIFIED BY 'HERE_CHANGE_USER_PASSWORD';

-- ----------------------------------------------------------------------------------------------------------------
-- Grant permissions for user on database `gameserver`
--

GRANT USAGE ON `gameserver`.* TO 'gameserver-api-user'@'localhost';
GRANT ALL PRIVILEGES ON `gameserver`.* TO `gameserver-api-user`@`localhost` REQUIRE NONE WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 MAX_USER_CONNECTIONS 0;

