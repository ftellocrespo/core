import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ErrorLog from './error-log';
import ErrorLogDetail from './error-log-detail';
import ErrorLogUpdate from './error-log-update';
import ErrorLogDeleteDialog from './error-log-delete-dialog';

const ErrorLogRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ErrorLog />} />
    <Route path="new" element={<ErrorLogUpdate />} />
    <Route path=":id">
      <Route index element={<ErrorLogDetail />} />
      <Route path="edit" element={<ErrorLogUpdate />} />
      <Route path="delete" element={<ErrorLogDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ErrorLogRoutes;
