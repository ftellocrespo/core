import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/process">
        <Translate contentKey="global.menu.entities.process" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/activity">
        <Translate contentKey="global.menu.entities.activity" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/action">
        <Translate contentKey="global.menu.entities.action" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/organization">
        <Translate contentKey="global.menu.entities.organization" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/user-x">
        <Translate contentKey="global.menu.entities.userX" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/organization-user">
        <Translate contentKey="global.menu.entities.organizationUser" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/error-log">
        <Translate contentKey="global.menu.entities.errorLog" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/action-generic">
        <Translate contentKey="global.menu.entities.actionGeneric" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
