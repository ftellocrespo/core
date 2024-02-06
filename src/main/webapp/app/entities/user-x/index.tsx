import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import UserX from './user-x';
import UserXDetail from './user-x-detail';
import UserXUpdate from './user-x-update';
import UserXDeleteDialog from './user-x-delete-dialog';

const UserXRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<UserX />} />
    <Route path="new" element={<UserXUpdate />} />
    <Route path=":id">
      <Route index element={<UserXDetail />} />
      <Route path="edit" element={<UserXUpdate />} />
      <Route path="delete" element={<UserXDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default UserXRoutes;
