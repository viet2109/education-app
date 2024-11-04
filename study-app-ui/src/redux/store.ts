import {
    combineReducers, configureStore,
} from "@reduxjs/toolkit";

import {
    persistReducer, persistStore, FLUSH,
    REHYDRATE,
    PAUSE,
    PERSIST,
    PURGE,
    REGISTER,
} from "redux-persist";
import storage from "redux-persist/lib/storage";
import authReducer from "./authSlice";
import appReducer from "./appSlice";

const authConfig = {
    key: 'auth',
    storage
}

const rootReducer = combineReducers({
    app: appReducer,
    auth: persistReducer(authConfig, authReducer)
});

export const store = configureStore({
    reducer: rootReducer,
    middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware({
            serializableCheck: {
                ignoredActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER],
            },
        }),
});

export const persistor = persistStore(store);

export type RootState = ReturnType<typeof store.getState>;
export const dispatch = store.dispatch;