import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Action from './action';
import ActionDetail from './action-detail';
import ActionUpdate from './action-update';
import ActionDeleteDialog from './action-delete-dialog';

const ActionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Action />} />
    <Route path="new" element={<ActionUpdate />} />
    <Route path=":id">
      <Route index element={<ActionDetail />} />
      <Route path="edit" element={<ActionUpdate />} />
      <Route path="delete" element={<ActionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ActionRoutes;
