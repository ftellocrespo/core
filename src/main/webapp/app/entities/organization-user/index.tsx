import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import OrganizationUser from './organization-user';
import OrganizationUserDetail from './organization-user-detail';
import OrganizationUserUpdate from './organization-user-update';
import OrganizationUserDeleteDialog from './organization-user-delete-dialog';

const OrganizationUserRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<OrganizationUser />} />
    <Route path="new" element={<OrganizationUserUpdate />} />
    <Route path=":id">
      <Route index element={<OrganizationUserDetail />} />
      <Route path="edit" element={<OrganizationUserUpdate />} />
      <Route path="delete" element={<OrganizationUserDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default OrganizationUserRoutes;
