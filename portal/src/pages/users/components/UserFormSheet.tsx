/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

import { Form as FormikForm, Formik } from 'formik';
import * as Yup from 'yup';

import { Button } from '@/components/ui/button';
import { Field, FieldGroup, FieldLabel } from '@/components/ui/field';
import { Input } from '@/components/ui/input';
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetFooter,
  SheetHeader,
  SheetTitle,
} from '@/components/ui/sheet';

import type { CreateUserRequest, UpdateUserRequest, UserDto } from '../types/user';

const createUserSchema = Yup.object().shape({
  email: Yup.string().email('Invalid email address').required('Email is required'),
  firstName: Yup.string(),
  lastName: Yup.string(),
  displayName: Yup.string(),
});

const editUserSchema = Yup.object().shape({
  email: Yup.string(),
  firstName: Yup.string(),
  lastName: Yup.string(),
  displayName: Yup.string(),
});

const getSubmitLabel = (isEdit: boolean) => (isEdit ? 'Update User' : 'Create User');

interface FormValues {
  email: string;
  firstName: string;
  lastName: string;
  displayName: string;
}

interface UserFormSheetProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  user: UserDto | null;
  onSubmit: (values: CreateUserRequest | UpdateUserRequest, userId?: string) => Promise<void>;
}

export const UserFormSheet = ({ open, onOpenChange, user, onSubmit }: UserFormSheetProps) => {
  const isEdit = !!user;

  const initialValues: FormValues = {
    email: user?.email ?? '',
    firstName: user?.firstName ?? '',
    lastName: user?.lastName ?? '',
    displayName: user?.displayName ?? '',
  };

  const handleSubmit = async (values: FormValues) => {
    if (isEdit) {
      const request: UpdateUserRequest = {
        email: user.email,
        firstName: values.firstName,
        lastName: values.lastName,
        displayName: values.displayName,
      };
      await onSubmit(request, user.id);
    } else {
      const request: CreateUserRequest = {
        email: values.email,
        firstName: values.firstName || undefined,
        lastName: values.lastName || undefined,
        displayName: values.displayName || undefined,
      };
      await onSubmit(request);
    }
    onOpenChange(false);
  };

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent>
        <SheetHeader>
          <SheetTitle>{isEdit ? 'Edit User' : 'Add User'}</SheetTitle>
          <SheetDescription>
            {isEdit
              ? 'Update the user details below.'
              : 'Fill in the details to create a new user.'}
          </SheetDescription>
        </SheetHeader>

        <Formik
          initialValues={initialValues}
          validationSchema={isEdit ? editUserSchema : createUserSchema}
          onSubmit={handleSubmit}
          enableReinitialize
        >
          {({ values, errors, touched, handleChange, handleBlur, isSubmitting }) => (
            <FormikForm className="flex flex-1 flex-col">
              <div className="flex-1 overflow-y-auto px-4">
                <FieldGroup>
                  <Field>
                    <FieldLabel htmlFor="email">Email</FieldLabel>
                    <Input
                      id="email"
                      name="email"
                      type="email"
                      placeholder="user@example.com"
                      value={values.email}
                      onChange={handleChange}
                      onBlur={handleBlur}
                      disabled={isEdit || isSubmitting}
                      className={touched.email && errors.email ? 'border-red-500' : ''}
                    />
                    {touched.email && errors.email && (
                      <p className="text-sm text-red-600">{errors.email}</p>
                    )}
                  </Field>

                  <Field>
                    <FieldLabel htmlFor="firstName">First Name</FieldLabel>
                    <Input
                      id="firstName"
                      name="firstName"
                      placeholder="John"
                      value={values.firstName}
                      onChange={handleChange}
                      onBlur={handleBlur}
                      disabled={isSubmitting}
                    />
                  </Field>

                  <Field>
                    <FieldLabel htmlFor="lastName">Last Name</FieldLabel>
                    <Input
                      id="lastName"
                      name="lastName"
                      placeholder="Doe"
                      value={values.lastName}
                      onChange={handleChange}
                      onBlur={handleBlur}
                      disabled={isSubmitting}
                    />
                  </Field>

                  <Field>
                    <FieldLabel htmlFor="displayName">Display Name</FieldLabel>
                    <Input
                      id="displayName"
                      name="displayName"
                      placeholder="johndoe"
                      value={values.displayName}
                      onChange={handleChange}
                      onBlur={handleBlur}
                      disabled={isSubmitting}
                    />
                  </Field>
                </FieldGroup>
              </div>

              <SheetFooter>
                <Button type="submit" disabled={isSubmitting} className="w-full">
                  {isSubmitting ? (
                    <span className="flex items-center gap-2">
                      <svg
                        className="h-4 w-4 animate-spin"
                        xmlns="http://www.w3.org/2000/svg"
                        fill="none"
                        viewBox="0 0 24 24"
                      >
                        <circle
                          className="opacity-25"
                          cx="12"
                          cy="12"
                          r="10"
                          stroke="currentColor"
                          strokeWidth="4"
                        />
                        <path
                          className="opacity-75"
                          fill="currentColor"
                          d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                        />
                      </svg>
                      {isEdit ? 'Updating...' : 'Creating...'}
                    </span>
                  ) : (
                    getSubmitLabel(isEdit)
                  )}
                </Button>
              </SheetFooter>
            </FormikForm>
          )}
        </Formik>
      </SheetContent>
    </Sheet>
  );
};
