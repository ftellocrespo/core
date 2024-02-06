import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Process from './process';
import Activity from './activity';
import Action from './action';
import Organization from './organization';
import UserX from './user-x';
import OrganizationUser from './organization-user';
import ErrorLog from './error-log';
import ActionGeneric from './action-generic';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="process/*" element={<Process />} />
        <Route path="activity/*" element={<Activity />} />
        <Route path="action/*" element={<Action />} />
        <Route path="organization/*" element={<Organization />} />
        <Route path="user-x/*" element={<UserX />} />
        <Route path="organization-user/*" element={<OrganizationUser />} />
        <Route path="error-log/*" element={<ErrorLog />} />
        <Route path="action-generic/*" element={<ActionGeneric />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
