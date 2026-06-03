import SwaggerUI from 'swagger-ui-react';
import 'swagger-ui-react/swagger-ui.css';

function ApiDocumentation() {
  return (
    <div style={{ padding: '20px' }}>
      <h1>Developer API Documentation</h1>
      {/* Serves the file straight out of your /public directory */}
      <SwaggerUI url="/api.yaml" />
    </div>
  );
}

export default ApiDocumentation;