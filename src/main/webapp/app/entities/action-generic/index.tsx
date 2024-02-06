import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ActionGeneric from './action-generic';
import ActionGenericDetail from './action-generic-detail';
import ActionGenericUpdate from './action-generic-update';
import ActionGenericDeleteDialog from './action-generic-delete-dialog';

const ActionGenericRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ActionGeneric />} />
    <Route path="new" element={<ActionGenericUpdate />} />
    <Route path=":id">
      <Route index element={<ActionGenericDetail />} />
      <Route path="edit" element={<ActionGenericUpdate />} />
      <Route path="delete" element={<ActionGenericDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ActionGenericRoutes;
