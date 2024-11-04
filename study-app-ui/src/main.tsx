import { createRoot } from "react-dom/client";
import App from "./App.tsx";
import "./index.css";
import { Provider } from "react-redux";
import { persistor, store } from "./redux/store.ts";
import { PersistGate } from "redux-persist/integration/react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
const queryClient = new QueryClient();

createRoot(document.getElementById("root")!).render(
  <Provider store={store}>
    <PersistGate persistor={persistor} loading={null}>
      <QueryClientProvider client={queryClient}>
        <App />
      </QueryClientProvider>
    </PersistGate>
  </Provider>
);
