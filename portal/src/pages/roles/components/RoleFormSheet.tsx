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

import type { CreateRoleRequest, RoleDto, UpdateRoleRequest } from '../types/role';

const createRoleSchema = Yup.object().shape({
  name: Yup.string().required('Name is required'),
  description: Yup.string(),
});

const editRoleSchema = Yup.object().shape({
  name: Yup.string(),
  description: Yup.string(),
});

const getSubmitLabel = (isEdit: boolean) => (isEdit ? 'Update Role' : 'Create Role');

interface FormValues {
  name: string;
  description: string;
}

interface RoleFormSheetProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  role: RoleDto | null;
  onSubmit: (values: CreateRoleRequest | UpdateRoleRequest, roleId?: string) => Promise<void>;
}

export const RoleFormSheet = ({ open, onOpenChange, role, onSubmit }: RoleFormSheetProps) => {
  const isEdit = !!role;

  const initialValues: FormValues = {
    name: role?.name ?? '',
    description: role?.description ?? '',
  };

  const handleSubmit = async (values: FormValues) => {
    if (isEdit) {
      const request: UpdateRoleRequest = {
        name: values.name,
        description: values.description,
      };
      await onSubmit(request, role.id);
    } else {
      const request: CreateRoleRequest = {
        name: values.name,
        description: values.description || undefined,
      };
      await onSubmit(request);
    }
    onOpenChange(false);
  };

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent>
        <SheetHeader>
          <SheetTitle>{isEdit ? 'Edit Role' : 'Add Role'}</SheetTitle>
          <SheetDescription>
            {isEdit
              ? 'Update the role details below.'
              : 'Fill in the details to create a new role.'}
          </SheetDescription>
        </SheetHeader>

        <Formik
          initialValues={initialValues}
          validationSchema={isEdit ? editRoleSchema : createRoleSchema}
          onSubmit={handleSubmit}
          enableReinitialize
        >
          {({ values, errors, touched, handleChange, handleBlur, isSubmitting }) => (
            <FormikForm className="flex flex-1 flex-col">
              <div className="flex-1 overflow-y-auto px-4">
                <FieldGroup>
                  <Field>
                    <FieldLabel htmlFor="name">Name</FieldLabel>
                    <Input
                      id="name"
                      name="name"
                      placeholder="admin"
                      value={values.name}
                      onChange={handleChange}
                      onBlur={handleBlur}
                      disabled={isSubmitting}
                      className={touched.name && errors.name ? 'border-red-500' : ''}
                    />
                    {touched.name && errors.name && (
                      <p className="text-sm text-red-600">{errors.name}</p>
                    )}
                  </Field>

                  <Field>
                    <FieldLabel htmlFor="description">Description</FieldLabel>
                    <Input
                      id="description"
                      name="description"
                      placeholder="Administrator role"
                      value={values.description}
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
